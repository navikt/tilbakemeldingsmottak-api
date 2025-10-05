package no.nav.tilbakemeldingsmottak

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tilbakemeldingsmottak.consumer.norg2.Enhet
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.DataJournalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.model.Answer
import no.nav.tilbakemeldingsmottak.model.DefaultAnswers
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.saf.generated.enums.Variantformat
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Bruker
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.DokumentInfo
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Dokumentvariant
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNAVN_1
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNAVN_2
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNAVN_3
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNAVN_4
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNAVN_5
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_1
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_2
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_3
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_4
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_5
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHET_STATUS
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_1
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_2
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_3
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_4
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_5
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

object TestUtils {
    const val PERSONNUMMER = "01010096460"
    const val AKTOERID = "1234567890123"
    const val DOKUMENT_INFO_ID = "dokumentInfoId"
    private val objectMapper = ObjectMapper().registerKotlinModule()

    object TestUtils {

        fun createHentSkjemaResponse(): HentSkjemaResponse {
            val schema = TestUtils::class.java.classLoader.getResourceAsStream("schema/schema.yaml")
            val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
            val classpathSkjema = schema?.bufferedReader(StandardCharsets.UTF_8).use { it?.readText() }
            var response = mapper.readValue(classpathSkjema, HentSkjemaResponse::class.java)

            val answers = listOf(NAV_KONTOR_1, NAV_KONTOR_2, NAV_KONTOR_3, NAV_KONTOR_4, NAV_KONTOR_5)
                .map { e -> Answer(answer = e) }

            response = response.copy(
                questions = response.questions?.map {
                    when (it.id) {
                        ENHETSNUMMER_PAAKLAGET, ENHETSNUMMER_BEHANDLENDE -> it.copy(answers = answers)
                        else -> it
                    }
                }
            )

            return response
        }

        fun createHentSkjemaResponseWithDefaultAnswers(): HentSkjemaResponse {
            val response = createHentSkjemaResponse()
            val answers = mutableMapOf(KANAL to KANAL_SERVICEKLAGESKJEMA_ANSWER)
            return response.copy(defaultAnswers = DefaultAnswers(answers = answers))
        }
    }

    fun createNorg2Response(): String {
        val enheter = listOf(
            Enhet(enhetNr = NAV_ENHETSNR_1, navn = NAV_ENHETSNAVN_1, status = NAV_ENHET_STATUS),
            Enhet(enhetNr = NAV_ENHETSNR_2, navn = NAV_ENHETSNAVN_2, status = NAV_ENHET_STATUS),
            Enhet(enhetNr = NAV_ENHETSNR_3, navn = NAV_ENHETSNAVN_3, status = NAV_ENHET_STATUS),
            Enhet(enhetNr = NAV_ENHETSNR_4, navn = NAV_ENHETSNAVN_4, status = NAV_ENHET_STATUS),
            Enhet(enhetNr = NAV_ENHETSNR_5, navn = NAV_ENHETSNAVN_5, status = NAV_ENHET_STATUS),
        )
        return objectMapper.writeValueAsString(enheter)
    }

    fun createSafGraphqlResponse(): String {
        val bruker = Bruker(PERSONNUMMER)
        val dokumentVarianter = listOf(
            Dokumentvariant(variantformat = Variantformat.ARKIV, saksbehandlerHarTilgang = true),
            Dokumentvariant(variantformat = Variantformat.SLADDET, saksbehandlerHarTilgang = true)
        )
        val dokumenter = listOf(DokumentInfo(DOKUMENT_INFO_ID, dokumentVarianter))
        val journalpost =
            Journalpost(bruker = bruker, datoOpprettet = LocalDateTime.now().toString(), dokumenter = dokumenter)

        val dataJournalpost = DataJournalpost()
        dataJournalpost.journalpost = journalpost
        val safJsonJournalpost = SafJsonJournalpost()
        safJsonJournalpost.data = dataJournalpost
        //return objectMapper.writeValueAsString(safJsonJournalpost.data)
        return "{\"data\": ${objectMapper.writeValueAsString(dataJournalpost)} }"
    }

    fun createSafGraphqlNoDocumentsResponse(): String {
        val bruker = Bruker(PERSONNUMMER)
        val dokumenter = listOf(DokumentInfo(DOKUMENT_INFO_ID, listOf()))
        val journalpost =
            Journalpost(bruker = bruker, datoOpprettet = LocalDateTime.now().toString(), dokumenter = dokumenter)
        return objectMapper.writeValueAsString(journalpost)
    }

    fun getStringFromByteArrayPdf(bytes: ByteArray?): String {
        val document = Loader.loadPDF(bytes)
        val stripper = PDFTextStripper()
        return stripper.getText(document)
    }
}
