package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createHentSkjemaResponse;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequest;

import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import org.junit.jupiter.api.Test;

class KlassifiserServiceklageValidatorTest {

    private KlassifiserServiceklageRequest klassifiserServiceklageRequest;
    private KlassifiserServiceklageValidator klassifiserServiceklageValidator = new KlassifiserServiceklageValidator();

    @Test
    void happyPathServiceklage() {
        klassifiserServiceklageRequest = createKlassifiserServiceklageRequest();
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse());
    }

}