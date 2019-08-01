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
    void shouldThrowExceptionIfPaaVegneAvBedriftNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setPaaVegneAvBedrift(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift er påkrevd dersom paaVegneAv=BEDRIFT"));
    }
}