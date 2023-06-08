package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import org.junit.jupiter.api.Test;

import static no.nav.tilbakemeldingsmottak.TestUtils.*;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.KANAL_MUNTLIG_ANSWER;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KlassifiserServiceklageValidatorTest {

    private final KlassifiserServiceklageValidator klassifiserServiceklageValidator = new KlassifiserServiceklageValidator();
    private KlassifiserServiceklageRequest klassifiserServiceklageRequest;

    @Test
    void happyPathServiceklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
    }

    @Test
    void happyPathAnnet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequestIkkeServiceklage();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
    }

    @Test
    void happyPathForvaltningsklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequestForvaltningsklage();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
    }

    @Test
    void shouldThrowExceptionIfChoiceNotValid() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setTEMA("Ugyldig valg");

        Exception thrown = assertThrows(ClientErrorException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=TEMA er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfTextIsBlank() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setAARSAK("");

        Exception thrown = assertThrows(ClientErrorException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=AARSAK er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfDateIsInvalid() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setFREMMETDATO("123");

        Exception thrown = assertThrows(ClientErrorException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=FREMMET_DATO er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfDefaultAnswerDoesntMatch() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        HentSkjemaResponse hentSkjemaResponse = createHentSkjemaResponseWithDefaultAnswers();
        klassifiserServiceklageRequest.setKANAL(KANAL_MUNTLIG_ANSWER);

        Exception thrown = assertThrows(ClientErrorException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, hentSkjemaResponse));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=KANAL matcher ikke svar i database"));
    }

}