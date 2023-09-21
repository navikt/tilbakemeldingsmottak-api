package no.nav.tilbakemeldingsmottak

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tilbakemeldingsmottak.consumer.norg2.Enhet
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.DataJournalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.model.Answer
import no.nav.tilbakemeldingsmottak.model.DefaultAnswers
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.ByteArrayInputStream
import java.io.InputStream
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

    @JvmStatic
    fun createNorg2Response(): String {
        val enheter = listOf(
            Enhet(NAV_ENHETSNR_1, NAV_ENHETSNAVN_1, NAV_ENHET_STATUS),
            Enhet(NAV_ENHETSNR_2, NAV_ENHETSNAVN_2, NAV_ENHET_STATUS),
            Enhet(NAV_ENHETSNR_3, NAV_ENHETSNAVN_3, NAV_ENHET_STATUS),
            Enhet(NAV_ENHETSNR_4, NAV_ENHETSNAVN_4, NAV_ENHET_STATUS),
            Enhet(NAV_ENHETSNR_5, NAV_ENHETSNAVN_5, NAV_ENHET_STATUS)
        )
        return objectMapper.writeValueAsString(enheter)
    }

    @JvmStatic
    fun createSafGraphqlResponse(): String {
        val bruker = SafJournalpostTo.Bruker(PERSONNUMMER)
        val dokumentVarianter = listOf(
            SafJournalpostTo.Dokumentvariant(Variantformat.ARKIV.name, true),
            SafJournalpostTo.Dokumentvariant(Variantformat.SLADDET.name, true)
        )
        val dokumenter = listOf(SafJournalpostTo.DokumentInfo(DOKUMENT_INFO_ID, dokumentVarianter))
        val safJournalpostTo = SafJournalpostTo(dokumenter, bruker, LocalDateTime.now().toString())
        val dataJournalpost = DataJournalpost()
        dataJournalpost.journalpost = safJournalpostTo
        val safJsonJournalpost = SafJsonJournalpost()
        safJsonJournalpost.data = dataJournalpost
        return objectMapper.writeValueAsString(safJsonJournalpost)
    }

    @JvmStatic
    fun createSafGraphqlNoDocumentsResponse(): String {
        val bruker = SafJournalpostTo.Bruker(PERSONNUMMER)
        val dokumenter = listOf(SafJournalpostTo.DokumentInfo(DOKUMENT_INFO_ID, listOf()))
        val safJournalpostTo = SafJournalpostTo(dokumenter, bruker, LocalDateTime.now().toString())
        val dataJournalpost = DataJournalpost()
        dataJournalpost.journalpost = safJournalpostTo
        val safJsonJournalpost = SafJsonJournalpost()
        safJsonJournalpost.data = dataJournalpost
        return objectMapper.writeValueAsString(safJsonJournalpost)
    }

    @JvmStatic
    fun getStringFromByteArrayPdf(bytes: ByteArray?): String {
        val documentStream: InputStream = ByteArrayInputStream(bytes)
        val document = PDDocument.load(documentStream)
        val stripper = PDFTextStripper()
        return stripper.getText(document)
    }
}
