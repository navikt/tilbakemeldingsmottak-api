package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator;
import org.junit.jupiter.api.Test;

class MeldFeilOgManglerValidatorTest {
    private MeldFeilOgManglerRequest meldFeilOgManglerRequest;
    private MeldFeilOgManglerValidator meldFeilOgManglerValidator = new MeldFeilOgManglerValidator();

    @Test
    void happyPath() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest);
    }

    @Test
    void shouldThrowExceptionIfNavnNotSet() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerRequest.setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest));
        assertTrue(thrown.getMessage().contains("navn er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfTelefonnummerNotSet() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerRequest.setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest));
        assertTrue(thrown.getMessage().contains("telefonnummer er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfFeiltypeNotSet() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerRequest.setFeiltype(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest));
        assertTrue(thrown.getMessage().contains("feiltype er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfMeldingNotSet() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerRequest.setMelding(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest));
        assertTrue(thrown.getMessage().contains("melding er påkrevd"));
    }

}