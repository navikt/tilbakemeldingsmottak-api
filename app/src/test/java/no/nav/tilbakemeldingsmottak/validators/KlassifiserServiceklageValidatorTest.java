package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequestNotServiceklage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import org.junit.jupiter.api.Test;

class KlassifiserServiceklageValidatorTest {

    private KlassifiserServiceklageRequest klassifiserServiceklageRequest;
    private KlassifiserServiceklageValidator klassifiserServiceklageValidator = new KlassifiserServiceklageValidator();

    @Test
    void happyPathServiceklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest);
    }

    @Test
    void happyPathNotServiceklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequestNotServiceklage();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest);
    }

    @Test
    void shouldThrowExceptionIfErServiceklageNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setErServiceklage(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("erServiceklage er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfKanalNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setKanal(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("kanal er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfPaaklagetEnhetNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setPaaklagetEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaklagetEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfBehandlendeEnhetNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setBehandlendeEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("behandlendeEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfYtelseTjenesteNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setYtelseTjeneste(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("ytelseTjeneste er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfTemaNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setTema(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("tema er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfUtfallNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setUtfall(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("utfall er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfSvarmetodeNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageRequest.setSvarmetode(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("svarmetode er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfGjelderNotSet() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequestNotServiceklage();
        klassifiserServiceklageRequest.setGjelder(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("gjelder er påkrevd dersom klagen ikke er en serviceklage"));
    }

}