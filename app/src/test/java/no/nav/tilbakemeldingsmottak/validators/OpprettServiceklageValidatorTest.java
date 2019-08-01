package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class OpprettServiceklageValidatorTest {

    private OpprettServiceklageRequest opprettServiceklageRequest;
    private OpprettServiceklageValidator opprettServiceklageValidator = new OpprettServiceklageValidator();

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
    void shouldThrowExceptionIfKlagetypeNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.setKlagetype(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("klagetype er påkrevd"));
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
    void shouldThrowExceptionIfInnmelderTelefonnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        opprettServiceklageRequest.getInnmelder().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.telefonnummer er påkrevd"));
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
        assertTrue(thrown.getMessage().contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
    }

    @Test
    void shouldThrowExceptionIfRolleNotSetforPaaVegneAvBedrift() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
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
    void shouldThrowExceptionIfPaaVegneAvBedriftPostadresseNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setPostadresse(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.postadresse er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftTelefonnummerNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getPaaVegneAvBedrift().setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift.telefonnummer er påkrevd"));
    }

}