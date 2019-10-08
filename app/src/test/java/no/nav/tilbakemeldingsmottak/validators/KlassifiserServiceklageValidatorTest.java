package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequestNotServiceklage;
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
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest);
    }

    @Test
    void happyPathNotServiceklage() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequestNotServiceklage();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest);
    }

    @Test
    void shouldThrowExceptionIfErServiceklageNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setErServiceklage(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("erServiceklage er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfKanalNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setKanal(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("kanal er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfPaaklagetEnhetNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setPaaklagetEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("paaklagetEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfBehandlendeEnhetNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setBehandlendeEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("behandlendeEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfYtelseTjenesteNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setYtelseTjeneste(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("ytelseTjeneste er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfTemaNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setTema(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("tema er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfUtfallNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setUtfall(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("utfall er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfSvarmetodeNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequest();
        klassifiserServiceklageRequest.setSvarmetode(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("svarmetode er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfGjelderNotSet() {
        klassifiserServiceklageRequest = createRegistrerTilbakemeldingRequestNotServiceklage();
        klassifiserServiceklageRequest.setGjelder(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest));
        assertTrue(thrown.getMessage().contains("gjelder er påkrevd dersom klagen ikke er en serviceklage"));
    }

}