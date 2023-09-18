package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklageEventType
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import no.nav.tilbakemeldingsmottak.consumer.joark.JournalpostConsumer
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageResponse
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettJournalpostRequestToMapper
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettServiceklageRequestMapper
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.ServiceklageMailHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OpprettServiceklageService(
    private val serviceklageRepository: ServiceklageRepository,
    private val opprettServiceklageRequestMapper: OpprettServiceklageRequestMapper,
    private val opprettJournalpostRequestToMapper: OpprettJournalpostRequestToMapper,
    private val journalpostConsumer: JournalpostConsumer,
    private val opprettOppgaveRequestToMapper: OpprettOppgaveRequestToMapper,
    private val oppgaveConsumer: OppgaveConsumer,
    private val pdfService: PdfService,
    private val mailHelper: ServiceklageMailHelper,
    private val serviceklagerBigQuery: ServiceklagerBigQuery
) {

    private val SUBJECT_JOURNALPOST_FEILET = "Automatisk journalføring av serviceklage feilet"
    private val TEXT_JOURNALPOST_FEILET =
        "Manuell journalføring og opprettelse av oppgave kreves. Klagen ligger vedlagt."
    private val SUBJECT_OPPGAVE_FEILET = "Automatisk opprettelse av oppgave feilet"
    private val TEXT_OPPGAVE_FEILET = "Manuell opprettelse av oppgave kreves for serviceklage med journalpostId="

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_serviceklage_address}")
    private lateinit var toAddress: String

    @Value("\${email_from_address}")
    private lateinit var fromAddress: String

    fun opprettServiceklage(request: OpprettServiceklageRequest, innlogget: Boolean): OpprettServiceklageResponse {
        val fysiskDokument = pdfService.opprettServiceklagePdf(request, innlogget)
        val opprettJournalpostResponseTo = forsoekOpprettJournalpost(request, fysiskDokument, innlogget)
        log.info("Journalpost med journalpostId={} opprettet", opprettJournalpostResponseTo.journalpostId)

        val serviceklage = opprettServiceklageRequestMapper.map(request, innlogget)
        serviceklage.journalpostId = opprettJournalpostResponseTo.journalpostId
        serviceklageRepository.save(serviceklage)
        serviceklagerBigQuery.insertServiceklage(serviceklage, ServiceklageEventType.OPPRETT_SERVICEKLAGE)
        log.info("Serviceklage med serviceklageId={} opprettet", serviceklage.serviceklageId)

        val opprettOppgaveResponseTo =
            forsoekOpprettOppgave(serviceklage.klagenGjelderId, request.paaVegneAv, opprettJournalpostResponseTo)
        log.info("Oppgave med oppgaveId={} opprettet", opprettOppgaveResponseTo.id)

        return OpprettServiceklageResponse(
            message = "Serviceklage opprettet",
            serviceklageId = serviceklage.serviceklageId.toString(),
            journalpostId = serviceklage.journalpostId,
            oppgaveId = opprettOppgaveResponseTo.id
        )
    }

    private fun forsoekOpprettJournalpost(
        request: OpprettServiceklageRequest,
        fysiskDokument: ByteArray,
        innlogget: Boolean
    ): OpprettJournalpostResponseTo {
        return try {
            val opprettJournalpostRequestTo =
                opprettJournalpostRequestToMapper.map(request, fysiskDokument, innlogget)
            journalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo)
        } catch (e: ClientErrorException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_JOURNALPOST_FEILET,
                TEXT_JOURNALPOST_FEILET,
                fysiskDokument
            )
            throw EksterntKallException(
                "Feil ved opprettelse av journalpost, klage videresendt til $toAddress",
                e,
                e.errorCode
            )
        } catch (e: ClientErrorUnauthorizedException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_JOURNALPOST_FEILET,
                TEXT_JOURNALPOST_FEILET,
                fysiskDokument
            )
            throw EksterntKallException(
                "Feil ved opprettelse av journalpost, klage videresendt til $toAddress",
                e,
                e.errorCode
            )
        } catch (e: ServerErrorException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_JOURNALPOST_FEILET,
                TEXT_JOURNALPOST_FEILET,
                fysiskDokument
            )
            throw EksterntKallException(
                "Feil ved opprettelse av journalpost, klage videresendt til $toAddress",
                e,
                e.errorCode
            )
        }
    }

    private fun forsoekOpprettOppgave(
        id: String?,
        paaVegneAv: PaaVegneAv?,
        opprettJournalpostResponseTo: OpprettJournalpostResponseTo
    ): OpprettOppgaveResponseTo {
        return try {
            val opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.mapServiceklageOppgave(
                id!!, paaVegneAv!!, opprettJournalpostResponseTo
            )
            oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo)
        } catch (e: ClientErrorException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_OPPGAVE_FEILET,
                TEXT_OPPGAVE_FEILET + opprettJournalpostResponseTo.journalpostId
            )
            throw EksterntKallException(
                "Feil ved opprettelse av oppgave, journalpostId videresendt til $toAddress",
                e,
                e.errorCode
            )
        } catch (e: ClientErrorUnauthorizedException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_OPPGAVE_FEILET,
                TEXT_OPPGAVE_FEILET + opprettJournalpostResponseTo.journalpostId
            )
            throw EksterntKallException(
                "Feil ved opprettelse av oppgave, journalpostId videresendt til $toAddress",
                e,
                e.errorCode
            )
        } catch (e: ServerErrorException) {
            mailHelper.sendEmail(
                fromAddress,
                toAddress,
                SUBJECT_OPPGAVE_FEILET,
                TEXT_OPPGAVE_FEILET + opprettJournalpostResponseTo.journalpostId
            )
            throw EksterntKallException(
                "Feil ved opprettelse av oppgave, journalpostId videresendt til $toAddress",
                e,
                e.errorCode
            )
        }
    }

}