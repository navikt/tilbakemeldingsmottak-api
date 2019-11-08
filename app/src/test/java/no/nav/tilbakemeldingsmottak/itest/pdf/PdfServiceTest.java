package no.nav.tilbakemeldingsmottak.itest.pdf;

import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatpersonLokaltKontor;
import static no.nav.tilbakemeldingsmottak.TestUtils.getStringFromByteArrayPdf;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class PdfServiceTest {

    private OpprettServiceklageRequest opprettServiceklageRequest;

    @Mock OidcUtils oidcUtils;
    @InjectMocks PdfService pdfService;

    @Before
    public void setup() {
        when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void happyPathPrivatperson() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
    }

    @Test
    public void happyPathAnnenPerson() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
    }

    @Test
    public void happyPathBedrift() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
    }

    @Test
    public void happyPathMultipageKlagetekst() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        String langKlagetekst = RandomStringUtils.randomAlphabetic(10000);
        opprettServiceklageRequest.setKlagetekst(langKlagetekst);
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
    }

    @Test
    public void happyPathLokaltKontor() throws DocumentException, IOException {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatpersonLokaltKontor();
        byte[] pdf = pdfService.opprettPdf(opprettServiceklageRequest);
        String content = getStringFromByteArrayPdf(pdf);

        assertPdfContainsContentFromRequest(opprettServiceklageRequest, content);
    }

    private void assertPdfContainsContentFromRequest(OpprettServiceklageRequest request, String content) {
        assertKlagetyper(request.getKlagetyper(), content);
        assertTrue(content.replace("\n", "").contains(request.getKlagetekst()));
        assertTrue(content.contains(KANAL_SERVICEKLAGESKJEMA_ANSWER));
        assertTrue(content.contains(request.getOenskerAaKontaktes() ?
                "Ønsker å kontaktes: Ja" : "Ønsker å kontaktes: Nei"));

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