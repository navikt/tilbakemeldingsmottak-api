package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklageEventType
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.consumer.saf.SafJournalpostQueryService
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost
import no.nav.tilbakemeldingsmottak.domain.Serviceklage
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ANNET
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.JA
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KOMMUNAL_KLAGE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.NONE
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorNotFoundException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.model.Answer
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.Question
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.ServiceklageMailHelper
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import no.nav.tilbakemeldingsmottak.util.SkjemaUtils.Companion.getQuestionById
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class KlassifiserServiceklageService(
    private val serviceklageRepository: ServiceklageRepository,
    private val oppgaveConsumer: OppgaveConsumer,
    private val endreOppgaveRequestToMapper: EndreOppgaveRequestToMapper,
    private val hentSkjemaService: HentSkjemaService,
    private val pdfService: PdfService,
    private val mailHelper: ServiceklageMailHelper,
    private val oidcUtils: OidcUtils,
    private val opprettOppgaveRequestToMapper: OpprettOppgaveRequestToMapper,
    private val serviceklagerBigQuery: ServiceklagerBigQuery,
    private val safJournalpostQueryService: SafJournalpostQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_serviceklage_address}")
    private lateinit var toAddress: String

    @Value("\${email_from_address}")
    private lateinit var fromAddress: String
    fun klassifiserServiceklage(request: KlassifiserServiceklageRequest, hentOppgaveResponseTo: HentOppgaveResponseTo) {
        if (KOMMUNAL_KLAGE == request.BEHANDLES_SOM_SERVICEKLAGE) {
            log.info("Klagen har blitt markert som en kommunal klage. Oppretter oppgave om sletting av dokument.")
            opprettSlettingOppgave(hentOppgaveResponseTo)
        }

        val serviceklage = getOrCreateServiceklage(hentOppgaveResponseTo.journalpostId)
        updateServiceklage(serviceklage, request)
        serviceklageRepository.save(serviceklage)
        serviceklagerBigQuery.insertServiceklage(serviceklage, ServiceklageEventType.KLASSIFISER_SERVICEKLAGE)

        log.info(
            "Serviceklage med serviceklageId={} er klassifisert som {}",
            serviceklage.serviceklageId,
            serviceklage.tema
        )
        log.info(
            "Ferdigstille oppgave med oppgaveId={} og versjonsnummer={}",
            hentOppgaveResponseTo.id,
            hentOppgaveResponseTo.versjon
        )

        ferdigstillOppgave(hentOppgaveResponseTo)
        log.info("Ferdigstilt oppgave med oppgaveId={}", hentOppgaveResponseTo.id)
        if (JA == request.KVITTERING) {
            try {
                sendKvittering(serviceklage, hentOppgaveResponseTo)
            } catch (e: Exception) {
                log.warn("Kunne ikke produsere kvittering på mail", e)
            }
        }
    }

    private fun sendKvittering(serviceklage: Serviceklage, hentOppgaveResponseTo: HentOppgaveResponseTo) {
        val email = oidcUtils.getEmailForIssuer(AZURE_ISSUER) ?: throw ClientErrorNotFoundException(
            message = "Fant ikke email-adresse i token",
            errorCode = ErrorCode.TOKEN_EMAIL_MISSING
        )

        log.info("Kvittering på innsendt klassifiseringsskjema sendes til epost: {}", email)

        val questionAnswerMap = createQuestionAnswerMap(serviceklage, hentOppgaveResponseTo)
        val pdf = pdfService.opprettKlassifiseringPdf(questionAnswerMap)
        mailHelper.sendEmail(
            fromAddress = fromAddress,
            toAddress = email,
            subject = "Kvittering på innsendt klassifiseringsskjema",
            text = "Serviceklage med oppgave-id " + hentOppgaveResponseTo.id + " har blitt klassifisert. " +
                    "Innholdet i ditt utfylte skjema ligger vedlagt.",
            fysiskDokument = pdf
        )
        log.info("Kvittering sendt på mail til saksbehandler")
    }

    private fun createQuestionAnswerMap(
        serviceklage: Serviceklage,
        hentOppgaveResponseTo: HentOppgaveResponseTo
    ): Map<String, String> {
        val questionAnswerMap = HashMap<String, String>()
        val skjemaResponse = if (hentOppgaveResponseTo.journalpostId != null) {
            hentSkjemaService.hentSkjema(hentOppgaveResponseTo.journalpostId)
        } else {
            hentSkjemaService.readSkjema()
        }
        val answersMap = serviceklage.klassifiseringJson?.let { klassifiseringsJson ->
            jacksonObjectMapper().readValue<Map<String, String?>>(klassifiseringsJson)
                .filterValues { value -> value != null }
        }

        if (answersMap != null) {
            skjemaResponse.questions?.let { addEntriesToQuestionAnswerMap(answersMap, it, questionAnswerMap) }
        }

        return questionAnswerMap
    }

    private fun addEntriesToQuestionAnswerMap(
        answersMap: Map<String, String?>,
        questions: List<Question>?,
        questionAnswerMap: MutableMap<String, String>
    ) {
        questions?.let { questionList ->
            for (question in questionList) {
                if (answersMap.containsKey(question.id) && !questionAnswerMap.containsKey(question.text)) {
                    val foundQuestion = question.id?.let { getQuestionById(questionList, it) }
                        ?: throw ServerErrorException("Finner ikke spørsmål med id=${question.id}")

                    val questionText = foundQuestion.text
                    if (questionText != null) {
                        questionAnswerMap[questionText] = answersMap[foundQuestion.id] ?: ""
                    }
                }

                if (question.type == Question.Type.RADIO) {
                    val answer = question.answers?.stream()
                        ?.filter { (answer): Answer -> answer == answersMap[question.id] }
                        ?.findFirst()
                    if (answer?.isPresent == true && answer.get().questions != null && answer.get().questions?.isNotEmpty() == true
                        && NONE != answer.get().next
                    ) {
                        addEntriesToQuestionAnswerMap(answersMap, answer.get().questions, questionAnswerMap)
                    }
                }
            }
        }
    }

    private fun opprettSlettingOppgave(hentOppgaveResponseTo: HentOppgaveResponseTo) {
        val opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.mapSlettingOppgave(hentOppgaveResponseTo)
        val oppgave = oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo)
        log.info("Opprettet oppgave med oppgaveId={}", oppgave.id)
    }

    private fun getOrCreateServiceklage(journalpostId: String?): Serviceklage {
        if (journalpostId == null) {
            throw ClientErrorException("JournalpostId kan ikke være null")
        }

        var serviceklage = serviceklageRepository.findByJournalpostId(journalpostId)
        if (serviceklage == null) {
            val journalpost = getJournalPost(journalpostId)
            serviceklage = Serviceklage(
                journalpostId = journalpostId,
                opprettetDato = journalpost.datoOpprettet,
                klagenGjelderId = journalpost.bruker.id
            )
        }
        return serviceklage
    }

    private fun getJournalPost(journalpostId: String): Journalpost {
        val authorizationHeader = "Bearer " + oidcUtils.getFirstValidToken()
        return safJournalpostQueryService.hentJournalpost(journalpostId, authorizationHeader)
    }

    private fun updateServiceklage(serviceklage: Serviceklage, request: KlassifiserServiceklageRequest) {
        serviceklage.behandlesSomServiceklage = request.BEHANDLES_SOM_SERVICEKLAGE
        serviceklage.behandlesSomServiceklageUtdypning = request.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING
        serviceklage.fremmetDato = if (request.FREMMET_DATO == null) null else LocalDate.parse(request.FREMMET_DATO)
        serviceklage.innsender = request.INNSENDER
        serviceklage.kanal = request.KANAL
        serviceklage.kanalUtdypning = request.KANAL_UTDYPNING
        serviceklage.enhetsnummerPaaklaget = extractEnhetsnummer(request.ENHETSNUMMER_PAAKLAGET)
        serviceklage.enhetsnummerBehandlende =
            if (JA == request.PAAKLAGET_ENHET_ER_BEHANDLENDE) extractEnhetsnummer(request.ENHETSNUMMER_PAAKLAGET) else extractEnhetsnummer(
                request.ENHETSNUMMER_BEHANDLENDE
            )
        serviceklage.gjelder = request.GJELDER
        serviceklage.beskrivelse = request.BESKRIVELSE
        serviceklage.ytelse = request.YTELSE
        serviceklage.relatert = request.RELATERT
        serviceklage.tema = request.TEMA
        serviceklage.temaUtdypning = mapTemaUtdypning(request)
        serviceklage.utfall = request.UTFALL
        serviceklage.aarsak = request.AARSAK
        serviceklage.tiltak = request.TILTAK
        serviceklage.svarmetode = request.SVARMETODE
        serviceklage.svarmetodeUtdypning = mapSvarmetodeUtdypning(request)
        serviceklage.avsluttetDato = LocalDateTime.now()
        try {
            serviceklage.klassifiseringJson = ObjectMapper().registerKotlinModule().writeValueAsString(request)
        } catch (e: JsonProcessingException) {
            throw ServerErrorException("Kan ikke konvertere klassifiseringsrequest til JSON-string", e)
        }
    }

    private fun ferdigstillOppgave(hentOppgaveResponseTo: HentOppgaveResponseTo) {
        val endreOppgaveRequestTo = endreOppgaveRequestToMapper.mapFerdigstillRequest(hentOppgaveResponseTo)
        oppgaveConsumer.endreOppgave(endreOppgaveRequestTo)
    }

    private fun mapTemaUtdypning(request: KlassifiserServiceklageRequest): String? {
        return if (!StringUtils.isBlank(request.VENTE)) {
            request.VENTE
        } else if (!StringUtils.isBlank(request.TILGJENGELIGHET)) {
            request.TILGJENGELIGHET
        } else if (!StringUtils.isBlank(request.INFORMASJON)) {
            request.INFORMASJON
        } else if (!StringUtils.isBlank(request.VEILEDNING)) {
            request.VEILEDNING
        } else if (!StringUtils.isBlank(request.TEMA_UTDYPNING)) {
            request.TEMA_UTDYPNING
        } else {
            null
        }
    }

    private fun mapSvarmetodeUtdypning(request: KlassifiserServiceklageRequest): String? {
        return if (!StringUtils.isBlank(request.SVAR_IKKE_NOEDVENDIG)) {
            if (request.SVAR_IKKE_NOEDVENDIG == ANNET) {
                request.SVARMETODE_UTDYPNING
            } else {
                request.SVAR_IKKE_NOEDVENDIG
            }
        } else {
            null
        }
    }

    private fun extractEnhetsnummer(enhet: String?): String? {
        if (StringUtils.isBlank(enhet)) {
            return null
        }
        val l = enhet?.trim { it <= ' ' }?.length
        if (l != null) {
            if (l >= 4) {
                val enhetsnummer = enhet.trim { it <= ' ' }.substring(l - 4)
                if (StringUtils.isNumeric(enhetsnummer)) {
                    return enhetsnummer
                }
            }
        }
        throw ClientErrorException("Klarer ikke å hente ut enhetsnummer for enhet=$enhet")
    }


}
