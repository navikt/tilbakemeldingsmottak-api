package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator;
import org.junit.jupiter.api.Test;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeldFeilOgManglerValidatorTest {

    private MeldFeilOgManglerValidator meldFeilOgManglerValidator = new MeldFeilOgManglerValidator();
    private MeldFeilOgManglerRequest meldFeilOgManglerRequest;

    @Test
    void happyPath() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest);
    }

    @Test
    void shouldThrowExceptionIfOnskerKontaktTrueAndEpostNotSet() {
        meldFeilOgManglerRequest = createMeldFeilOgManglerRequest();
        meldFeilOgManglerRequest.setOnskerKontakt(true);
        meldFeilOgManglerRequest.setEpost(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest));
        assertTrue(thrown.getMessage().contains("epost er påkrevd dersom onskerKontakt=true"));
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