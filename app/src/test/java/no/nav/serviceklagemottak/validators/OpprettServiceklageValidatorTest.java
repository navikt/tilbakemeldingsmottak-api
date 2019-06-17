package no.nav.serviceklagemottak.validators;

import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.exceptions.InvalidRequestException;
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
        assertTrue(thrown.getMessage().contains("innmelder.personnummer kan ikke være null eller tom dersom paaVegneAv=PRIVATPERSON"));
    }

    @Test
    void shouldThrowExceptionIfHarFullmaktNotSetForPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setHarFullmakt(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.harFullmakt kan ikke være null dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    void shouldThrowExceptionIfRolleNotSetforPaaVegneAvPerson() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle kan ikke være null eller tom dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
    }

    @Test
    void shouldThrowExceptionIfRolleNotSetforPaaVegneAvBedrift() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.getInnmelder().setRolle(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("innmelder.rolle kan ikke være null eller tom dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvPersonNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        opprettServiceklageRequest.setPaaVegneAvPerson(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvPerson kan ikke være null dersom paaVegneAv=ANNEN_PERSON"));
    }

    @Test
    void shouldThrowExceptionIfPaaVegneAvBedriftNotSet() {
        opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        opprettServiceklageRequest.setPaaVegneAvBedrift(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> opprettServiceklageValidator.validateRequest(opprettServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaVegneAvBedrift kan ikke være null dersom paaVegneAv=BEDRIFT"));
    }
}