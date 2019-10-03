package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequestNotServiceklage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.RegistrerTilbakemeldingValidator;
import org.junit.jupiter.api.Test;

class RegistrerTilbakemeldingValidatorTest {

    private RegistrerTilbakemeldingRequest registrerTilbakemeldingRequest;
    private RegistrerTilbakemeldingValidator registrerTilbakemeldingValidator = new RegistrerTilbakemeldingValidator();

    @Test
    void happyPathServiceklage() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest);
    }

    @Test
    void happyPathNotServiceklage() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequestNotServiceklage();
        registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest);
    }

    @Test
    void shouldThrowExceptionIfErServiceklageNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setErServiceklage(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("erServiceklage er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfKanalNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setKanal(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("kanal er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfPaaklagetEnhetNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setPaaklagetEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("paaklagetEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfBehandlendeEnhetNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setBehandlendeEnhet(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("behandlendeEnhet er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfYtelseTjenesteNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setYtelseTjeneste(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("ytelseTjeneste er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfTemaNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setTema(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("tema er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfUtfallNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setUtfall(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("utfall er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfSvarmetodeNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequest();
        registrerTilbakemeldingRequest.setSvarmetode(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("svarmetode er påkrevd dersom klagen er en serviceklage"));
    }

    @Test
    void shouldThrowExceptionIfGjelderNotSet() {
        registrerTilbakemeldingRequest = createRegistrerTilbakemeldingRequestNotServiceklage();
        registrerTilbakemeldingRequest.setGjelder(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> registrerTilbakemeldingValidator.validateRequest(registrerTilbakemeldingRequest));
        assertTrue(thrown.getMessage().contains("gjelder er påkrevd dersom klagen ikke er en serviceklage"));
    }

}