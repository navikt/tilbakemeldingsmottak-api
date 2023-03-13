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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import no.nav.tilbakemeldingsmottak.consumer.aktoer.AktoerConsumer;
import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidIdentException;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregFunctionalException;
import no.nav.tilbakemeldingsmottak.rest.common.validation.PersonnummerValidator;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OpprettServiceklageValidatorTest {

    private OpprettServiceklageRequest opprettServiceklageRequest;

    @Mock EregConsumer eregConsumer;
    @Mock AktoerConsumer aktoerConsumer;
    @Mock OidcUtils oidcUtils;
    @Mock PersonnummerValidator personnummerValidator;
    @InjectMocks OpprettServiceklageValidator opprettServiceklageValidator;

    @BeforeEach
    void setup() {
        lenient().when(eregConsumer.hentInfo(anyString())).thenReturn("");
        lenient().when(aktoerConsumer.hentAktoerIdForIdent(anyString())).thenReturn(createHentAktoerIdForIdentResponse(PERSONNUMMER));
        lenient().when(oidcUtils.getSubjectForIssuer(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void happyPathPrivatperson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }
    @Test
    void happyPathPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }

    @Test
    void happyPathPaaVegneAvBedrift() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest);
    }

    @Test
    void happyPathInnlogget() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest, opprettServiceklageRequest.getInnmelder().getPersonnummer());
    }

    @Test
    void shouldThrowExceptionIfKlagetypeNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("klagetyper er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfGjelderSosialhjelpNotSetForLokaltkontor() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetyper(Collections.singletonList(KlagetyperEnum.LOKALT_NAV_KONTOR));
        opprettServiceklageRequest.setGjelderSosialhjelp(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("gjelderSosialhjelp er påkrevd dersom klagetyper=LOKALT_NAV_KONTOR"));
    }

    @Test
    void shouldThrowExceptionIfKlagetekstNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetekst(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("klagetekst er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfOenskerAaKontaktesNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setOenskerAaKontaktes(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("oenskerAaKontaktes er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setPaaVegneAv(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAv er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfInnmelderNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setInnmelder(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfInnmelderNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.getInnmelder().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.navn er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfInnmelderTelefonnummerNotSetAndOenskerAaKontaktesIsTrue() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    void shouldThrowExceptionIfPersonnummerNotSetForPrivatperson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.getInnmelder().setPersonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.personnummer er påkrevd dersom paaVegneAv=PRIVATPERSON"));
    }

    @Test
    void shouldThrowExceptionIfHarFullmaktNotSetForPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setHarFullmakt(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.harFullmakt er påkrevd dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    void shouldThrowExceptionIfRolleNotSetforPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.setPaaVegneAvPerson(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson er påkrevd dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getPaaVegneAvPerson().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson.navn er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonPersonnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getPaaVegneAvPerson().setPersonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson.personnummer er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonAndOenskerAaKontaktesIsSetWithoutFullmakt() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setHarFullmakt(false);
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("oenskerAaKontaktes kan ikke være satt dersom klagen er meldt inn på vegne av annen person uten fullmakt"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittNavn() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setOenskerAaKontaktes(true);
        opprettServiceklageRequest.getInnmelder().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.navn er påkrevd dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setPaaVegneAvBedrift(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift er påkrevd dersom paaVegneAv=BEDRIFT"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftNavnNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.navn er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftOrganisasjonsnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setOrganisasjonsnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.organisasjonsnummer er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setEnhetsnummerPaaklaget(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("enhetsnummerPaaklaget er påkrevd dersom paaVegneAv=BEDRIFT"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerWrongFormat() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setEnhetsnummerPaaklaget("123abc");
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("enhetsnummerPaaklaget må ha fire siffer"));
    }

    @Test
    void shouldThrowExceptionIfPersonnummerNotValid() {
        when(aktoerConsumer.hentAktoerIdForIdent(anyString())).thenReturn(createInvalidHentAktoerIdForIdentResponse(PERSONNUMMER));
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        Exception thrown = assertThrows(InvalidIdentException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("Feil i validering av personnummer"));
    }

    @Test
    void shouldThrowExceptionIfOrganisasjonsnummerNotValid() {
        when(eregConsumer.hentInfo(anyString())).thenThrow(EregFunctionalException.class);
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        Exception thrown = assertThrows(InvalidIdentException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("Feil i validering av organisasjonsnummer"));
    }

    @Test
    void shouldThrowExceptionIfPersonnummerDoesntMatchTokenIdent() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest,"12345678901"));
        assertTrue(thrown.getMessage().contains("innmelder.personnummer samsvarer ikke med brukertoken"));
    }
}