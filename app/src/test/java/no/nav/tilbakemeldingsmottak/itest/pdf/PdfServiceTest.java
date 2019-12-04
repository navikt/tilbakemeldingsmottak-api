package no.nav.tilbakemeldingsmottak.itest.pdf;

import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatpersonLokaltKontor;
import static no.nav.tilbakemeldingsmottak.TestUtils.getStringFromByteArrayPdf;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.itextpdf.text.DocumentException;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Innmelder;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvBedrift;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    private OpprettServiceklageRequest opprettServiceklageRequest;

    @Mock OidcUtils oidcUtils;
    @InjectMocks PdfService pdfService;

    @BeforeEach
    void setup() {
        lenient().when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void happyPathPrivatperson() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathAnnenPerson() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathBedrift() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathMultipageKlagetekst() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        String langKlagetekst = RandomStringUtils.randomAlphabetic(10000);
        opprettServiceklageRequest.setKlagetekst(langKlagetekst);
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathLokaltKontor() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatpersonLokaltKontor();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathSpesifisertKlagetype() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(Collections.singletonList(Klagetype.ANNET));
        opprettServiceklageRequest.setKlagetypeUtdypning("Spesifisert");
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathInnlogget() throws DocumentException, IOException {
        when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.of(PERSONNUMMER));
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertFalse(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    private void assertPdfContainsContentFromRequest(OpprettServiceklageRequest request, String content) {
        assertKlagetyper(request.getKlagetyper(), content);
        assertTrue(content.replace("\n", "").contains(request.getKlagetekst()));
        assertTrue(content.contains(KANAL_SERVICEKLAGESKJEMA_ANSWER));
        assertTrue(content.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        assertTrue(content.contains(request.getOenskerAaKontaktes() ?
                "Ønsker å kontaktes: Ja" : "Ønsker å kontaktes: Nei"));
        assertContainsIfNotNull(content, request.getKlagetypeUtdypning());
        assertContainsIfNotNull(content, request.getEnhetsnummerPaaklaget());
        if (request.getGjelderSosialhjelp() != null) {
            assertTrue(content.contains("Gjelder økonomisk sosialhjelp/sosiale tjenester: " + request.getGjelderSosialhjelp().text));
        }

        assertInnmelder(request.getInnmelder(), content);
        if (request.getPaaVegneAv().equals(PaaVegneAvType.ANNEN_PERSON)) {
            assertAnnenPerson(request.getPaaVegneAvPerson(), content);
        }
        if (request.getPaaVegneAv().equals(PaaVegneAvType.BEDRIFT)) {
            assertBedrift(request.getPaaVegneAvBedrift(), content);
        }
    }

    private void assertKlagetyper(List<Klagetype> klagetyper, String content) {
        for (Klagetype k : klagetyper) {
            assertTrue(content.contains(k.text));
        }
    }

    private void assertInnmelder(Innmelder innmelder, String content) {
        assertContainsIfNotNull(content, innmelder.getNavn());
        assertContainsIfNotNull(content, innmelder.getTelefonnummer());
        assertContainsIfNotNull(content, innmelder.getPersonnummer());
        assertContainsIfNotNull(content, innmelder.getRolle());
        if (innmelder.getHarFullmakt() != null) {
            assertTrue(content.contains(opprettServiceklageRequest.getInnmelder().getHarFullmakt() ?
                    "Innmelder har fullmakt: Ja" : "Innmelder har fullmakt: Nei"));
        }
    }

    private void assertAnnenPerson(PaaVegneAvPerson paaVegneAvPerson, String content) {
        assertContainsIfNotNull(content, paaVegneAvPerson.getNavn());
        assertContainsIfNotNull(content, paaVegneAvPerson.getPersonnummer());
    }

    private void assertBedrift(PaaVegneAvBedrift paaVegneAvBedrift, String content) {
        assertContainsIfNotNull(content, paaVegneAvBedrift.getNavn());
        assertContainsIfNotNull(content, paaVegneAvBedrift.getOrganisasjonsnummer());

    }

    private void assertContainsIfNotNull(String content, String subcontent) {
        if (subcontent != null) {
            assertTrue(content.contains(subcontent));
        }
    }
}