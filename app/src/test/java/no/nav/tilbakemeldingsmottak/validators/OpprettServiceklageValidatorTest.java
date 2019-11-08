package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.createHentAktoerIdForIdentResponse;
import static no.nav.tilbakemeldingsmottak.TestUtils.createInvalidHentAktoerIdForIdentResponse;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import no.nav.tilbakemeldingsmottak.consumer.aktoer.AktoerConsumer;
import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidIdentException;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregFunctionalException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class OpprettServiceklageValidatorTest {

    private OpprettServiceklageRequest opprettServiceklageRequest;

    @Mock EregConsumer eregConsumer;
    @Mock AktoerConsumer aktoerConsumer;
    @Mock OidcUtils oidcUtils;
    @InjectMocks OpprettServiceklageValidator opprettServiceklageValidator;

    @Before
    public void setup() {
        when(eregConsumer.hentInfo(anyString())).thenReturn("");
        when(aktoerConsumer.hentAktoerIdForIdent(anyString())).thenReturn(createHentAktoerIdForIdentResponse(PERSONNUMMER));
        when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void happyPathPrivatperson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }
    @Test
    public void happyPathPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }

    @Test
    public void happyPathPaaVegneAvBedrift() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }

    @Test
    public void happyPathInnlogget() {
        when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.of(PERSONNUMMER));
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }

    @Test
    public void shouldThrowExceptionIfKlagetypeNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("klagetyper er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfGjelderSosialhjelpNotSetForLokaltkontor() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(Collections.singletonList(Klagetype.LOKALT_NAV_KONTOR));
        opprettServiceklageRequest.setGjelderSosialhjelp(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("gjelderSosialhjelp er påkrevd dersom klagetyper=LOKALT_NAV_KONTOR"));
    }

    @Test
    public void shouldThrowExceptionIfKlagetekstNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetekst(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("klagetekst er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfOenskerAaKontaktesNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setOenskerAaKontaktes(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("oenskerAaKontaktes er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setPaaVegneAv(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAv er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfInnmelderNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setInnmelder(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfInnmelderNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.getInnmelder().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.navn er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfInnmelderTelefonnummerNotSetAndOenskerAaKontaktesIsTrue() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    public void shouldThrowExceptionIfPersonnummerNotSetForPrivatperson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.getInnmelder().setPersonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.personnummer er påkrevd dersom paaVegneAv=PRIVATPERSON"));
    }

    @Test
    public void shouldThrowExceptionIfHarFullmaktNotSetForPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setHarFullmakt(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.harFullmakt er påkrevd dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    public void shouldThrowExceptionIfRolleNotSetforPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
    }

    @Test
    public void shouldThrowExceptionIfRolleNotSetforPaaVegneAvBedrift() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvPersonNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.setPaaVegneAvPerson(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson er påkrevd dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvPersonNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getPaaVegneAvPerson().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson.navn er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvPersonPersonnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getPaaVegneAvPerson().setPersonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson.personnummer er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvPersonAndOenskerAaKontaktesIsSetWithoutFullmakt() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setHarFullmakt(false);
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("oenskerAaKontaktes kan ikke være satt dersom klagen er meldt inn på vegne av annen person uten fullmakt"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvPersonAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittNavn() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.navn er påkrevd dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setPaaVegneAvBedrift(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift er påkrevd dersom paaVegneAv=BEDRIFT"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.navn er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftOrganisasjonsnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setOrganisasjonsnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.organisasjonsnummer er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setEnhetsnummerPaaklaget(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("enhetsnummerPaaklaget er påkrevd dersom paaVegneAv=BEDRIFT"));
    }

    @Test
    public void shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerWrongFormat() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setEnhetsnummerPaaklaget("123abc");
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("enhetsnummerPaaklaget må ha fire siffer"));
    }

    @Test
    public void shouldThrowExceptionIfPersonnummerNotValid() {
        when(aktoerConsumer.hentAktoerIdForIdent(anyString())).thenReturn(createInvalidHentAktoerIdForIdentResponse(PERSONNUMMER));
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        Exception thrown = assertThrows(InvalidIdentException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("Oppgitt personnummer er ikke gyldig"));
    }

    @Test
    public void shouldThrowExceptionIfOrganisasjonsnummerNotValid() {
        when(eregConsumer.hentInfo(anyString())).thenThrow(EregFunctionalException.class);
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        Exception thrown = assertThrows(InvalidIdentException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("Oppgitt organisasjonsnummer er ikke gyldig"));
    }

    @Test
    public void shouldThrowExceptionIfPersonnummerDoesntMatchTokenIdent() {
        when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.of("12345678901"));
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.personnummer samsvarer ikke med brukertoken"));
    }
}