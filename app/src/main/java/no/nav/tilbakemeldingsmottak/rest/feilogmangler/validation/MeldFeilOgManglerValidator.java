package no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation;

import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;

public class MeldFeilOgManglerValidator implements RequestValidator {

    public void validateRequest(MeldFeilOgManglerRequest request) {
        hasText(request.getNavn(), "navn");
        hasText(request.getTelefonnummer(), "telefonnummer");
        hasText(request.getEpost(), "epost");
        isNotNull(request.getFeiltype(), "feiltype");
        hasText(request.getMelding(), "melding");
    }

}
