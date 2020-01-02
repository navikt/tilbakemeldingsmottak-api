package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createHentSkjemaResponse;
import static no.nav.tilbakemeldingsmottak.TestUtils.createHentSkjemaResponseWithDefaultAnswers;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequest;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_MUNTLIG_ANSWER;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import org.junit.jupiter.api.Test;

class KlassifiserServiceklageValidatorTest {

    private KlassifiserServiceklageValidator klassifiserServiceklageValidator = new KlassifiserServiceklageValidator();
    private KlassifiserServiceklageRequest klassifiserServiceklageRequest;

    @Test
    void happyPathServiceklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
    }

//    @Test
//    void happyPathAnnet() {
//        klassifiserServiceklageRequest = createKlassifiserServiceklageRequestIkkeServiceklage();
//        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
//    }

    @Test
    void shouldThrowExceptionIfChoiceNotValid() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setTema("Ugyldig valg");

        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=TEMA er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfTextIsBlank() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setAarsak("");

        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=AARSAK er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfDateIsInvalid() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setFremmetDato("123");

        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse()));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=FREMMET_DATO er ikke gyldig"));
    }

    @Test
    void shouldThrowExceptionIfDefaultAnswerDoesntMatch() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        HentSkjemaResponse hentSkjemaResponse = createHentSkjemaResponseWithDefaultAnswers();
        klassifiserServiceklageRequest.setKanal(KANAL_MUNTLIG_ANSWER);

        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, hentSkjemaResponse));
        assertTrue(thrown.getMessage().contains("Innsendt svar på spørsmål med id=KANAL matcher ikke svar i database"));
    }

}