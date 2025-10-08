package no.nav.tilbakemeldingsmottak.itest.pdf

import no.nav.tilbakemeldingsmottak.TestUtils.getStringFromByteArrayPdf
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.model.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.ANNEN_PERSON
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.BEDRIFT
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import no.nav.tilbakemeldingsmottak.util.builders.OpprettServiceklageRequestBuilder
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExtendWith(MockitoExtension::class)
internal class PdfServiceTest {
    @Mock
    var oidcUtils: OidcUtils? = null

    @InjectMocks
    var pdfService: PdfService? = null
    private var opprettServiceklageRequest: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().build()

    @BeforeEach
    fun setup() {
        Mockito.lenient().`when`(oidcUtils!!.getSubject())
            .thenReturn("")
    }

    @Test
    fun happyPathPrivatperson() {
        // Given
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))


    }

    @Test
    fun happyPathAnnenPerson() {
        // Given
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build()

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathBedrift() {
        // Given
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build()

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathMultipageKlagetekst() {
        // Given
        val langKlagetekst = RandomStringUtils.randomAlphabetic(10000)
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().build(klagetekst = langKlagetekst)

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathLokaltKontor() {
        // Given
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().withLokaltKontorKlagetype().build()

        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathSpesifisertKlagetype() {
        // Given
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().withAnnetKlagetype().build()

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, false)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathInnlogget() {
        // Given
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, true)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content)
        assertFalse(content.contains("OBS! Klagen er sendt inn uinnlogget"))
    }

    @Test
    fun happyPathMedEmojis() {
        // Given
        val tekstMedEmojis = "Heisann 😊"
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().build(klagetekst = tekstMedEmojis)

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, true)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertTrue(content.contains("Heisann"))
        assertFalse(content.contains("😊"))
    }

    @Test
    fun happyPathMedKontrollKarakterer() {
        // Given
        val tekstMedKontrollKarakterer = "Heisann \r\t\u0001\u0000sveisann"
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().build(klagetekst = tekstMedKontrollKarakterer)

        // When
        val pdf = pdfService!!.opprettServiceklagePdf(opprettServiceklageRequest, true)
        val content = getStringFromByteArrayPdf(pdf)

        // Then
        assertTrue(content.contains("Heisann sveisann"))
    }

    private fun assertPdfContainsContentFromRequest(request: OpprettServiceklageRequest?, content: String) {
        assertKlagetyper(request!!.klagetyper, content)
        val now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val regex = "$now Side  av [1-9] [1-9]"
        assertTrue(content.replace("\n", "").replace(regex.toRegex(), "").contains(request.klagetekst!!))
        assertTrue(content.contains(KANAL_SERVICEKLAGESKJEMA_ANSWER))
        assertTrue(content.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
        assertTrue(content.contains(if (request.oenskerAaKontaktes!!) "Ønsker å kontaktes: Ja" else "Ønsker å kontaktes: Nei"))
        assertContainsIfNotNull(content, request.klagetypeUtdypning)
        assertContainsIfNotNull(content, request.enhetsnummerPaaklaget)
        if (request.gjelderSosialhjelp != null) {
            assertTrue(
                content.contains(
                    """
                    Gjelder økonomisk sosialhjelp
                    /sosiale tjenester:
                    ${request.gjelderSosialhjelp!!.value}
                    """.trimIndent()
                )
            )
        }
        assertInnmelder(request.innmelder, content)
        if (request.paaVegneAv == ANNEN_PERSON) {
            assertAnnenPerson(request.paaVegneAvPerson, content)
        }
        if (request.paaVegneAv == BEDRIFT) {
            assertBedrift(request.paaVegneAvBedrift, content)
        }
    }

    private fun assertKlagetyper(klagetyper: List<OpprettServiceklageKlagetype>?, content: String) {
        for (k in klagetyper!!) {
            assertTrue(content.contains(k.value))
        }
    }

    private fun assertInnmelder(innmelder: Innmelder?, content: String) {
        assertContainsIfNotNull(content, innmelder!!.navn)
        assertContainsIfNotNull(content, innmelder.telefonnummer)
        assertContainsIfNotNull(content, innmelder.personnummer)
        assertContainsIfNotNull(content, innmelder.rolle)
        if (innmelder.harFullmakt != null) {
            assertTrue(content.contains(if (opprettServiceklageRequest.innmelder!!.harFullmakt!!) "Innmelder har fullmakt: Ja" else "Innmelder har fullmakt: Nei"))
        }
    }

    private fun assertAnnenPerson(paaVegneAvPerson: PaaVegneAvPerson?, content: String) {
        assertContainsIfNotNull(content, paaVegneAvPerson!!.navn)
        assertContainsIfNotNull(content, paaVegneAvPerson.personnummer)
    }

    private fun assertBedrift(paaVegneAvBedrift: PaaVegneAvBedrift?, content: String) {
        assertContainsIfNotNull(content, paaVegneAvBedrift!!.navn)
        assertContainsIfNotNull(content, paaVegneAvBedrift.organisasjonsnummer)
    }

    private fun assertContainsIfNotNull(content: String, subcontent: String?) {
        if (subcontent != null) {
            assertTrue(content.contains(subcontent))
        }
    }

    // Write PDF file to disk for manual inspection
    fun writeByteArrayToPDF(byteArray: ByteArray, outputPath: String) {
        Files.write(Paths.get(outputPath), byteArray)
    }

}