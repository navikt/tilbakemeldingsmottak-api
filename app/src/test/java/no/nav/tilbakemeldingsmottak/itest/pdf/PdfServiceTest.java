package no.nav.tilbakemeldingsmottak.itest.pdf;

import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatpersonLokaltKontor;
import static no.nav.tilbakemeldingsmottak.TestUtils.getStringFromByteArrayPdf;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.model.Innmelder;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAvEnum;
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvBedrift;
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    void happyPathPrivatperson() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));

/*
        File file = new File(this.getClass().getClassLoader().getResource(".").getFile() + "/serviceklagePrivatePerson.pdf");
        Files.write(file.toPath(), pdf);
*/
    }

    @Test
    void happyPathAnnenPerson() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathBedrift() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathMultipageKlagetekst() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        String langKlagetekst = RandomStringUtils.randomAlphabetic(10000);
        opprettServiceklageRequest.setKlagetekst(langKlagetekst);
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathLokaltKontor() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatpersonLokaltKontor();
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathSpesifisertKlagetype() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(Collections.singletonList(KlagetyperEnum.ANNET));
        opprettServiceklageRequest.setKlagetypeUtdypning("Spesifisert");
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, false);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertTrue(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathInnlogget() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, true);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
        assertFalse(content.contains("OBS! Klagen er sendt inn uinnlogget"));
    }

    @Test
    void happyPathMedEmojis() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        String tekstMedEmojis = "Heisann 😊";
        opprettServiceklageRequest.setKlagetekst(tekstMedEmojis);
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, true);
        String content = getStringFromByteArrayPdf(pdf);

        assertTrue(content.contains("Heisann"));
        assertFalse(content.contains("😊"));
    }

    @Test
    void happyPathMedKontrollKarakterer() throws IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        String tekstMedEmojis = "Heisann \n\r\t\u0001\u0000sveisann";
        opprettServiceklageRequest.setKlagetekst(tekstMedEmojis);
        byte[] pdf = pdfService.opprettServiceklagePdf(opprettServiceklageRequest, true);
        String content = getStringFromByteArrayPdf(pdf);
        assertTrue(content.contains("Heisann sveisann"));
    }

    private void assertPdfContainsContentFromRequest(OpprettServiceklageRequest request, String content) {
        assertKlagetyper(request.getKlagetyper(), content);
        String now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String regex = "Side [1-9] / [1-9]" + now;
        assertTrue(content.replace("\n", "").replaceAll(regex, "").contains(request.getKlagetekst()));
        assertTrue(content.contains(KANAL_SERVICEKLAGESKJEMA_ANSWER));
        assertTrue(content.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        assertTrue(content.contains(request.getOenskerAaKontaktes() ?
                "Ønsker å kontaktes: Ja" : "Ønsker å kontaktes: Nei"));
        assertContainsIfNotNull(content, request.getKlagetypeUtdypning());
        assertContainsIfNotNull(content, request.getEnhetsnummerPaaklaget());
        if (request.getGjelderSosialhjelp() != null) {
            assertTrue(content.replace("\n"," ").contains("Gjelder økonomisk sosialhjelp/sosiale tjenester: " + request.getGjelderSosialhjelp().getValue()));
        }

        assertInnmelder(request.getInnmelder(), content);
        if (request.getPaaVegneAv().equals(PaaVegneAvEnum.ANNEN_PERSON)) {
            assertAnnenPerson(request.getPaaVegneAvPerson(), content);
        }
        if (request.getPaaVegneAv().equals(PaaVegneAvEnum.BEDRIFT)) {
            assertBedrift(request.getPaaVegneAvBedrift(), content);
        }
    }

    private void assertKlagetyper(List<KlagetyperEnum> klagetyper, String content) {
        for (KlagetyperEnum k : klagetyper) {
            assertTrue(content.contains(k.getValue()));
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