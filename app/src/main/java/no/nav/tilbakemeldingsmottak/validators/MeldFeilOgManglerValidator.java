package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;

public class MeldFeilOgManglerValidator implements RequestValidator {

    public void validateRequest(MeldFeilOgManglerRequest request) {
        hasText(request.getNavn(), "navn");
        hasText(request.getTelefonnummer(), "telefonnummer");
        isNotNull(request.getFeiltype(), "feiltype");
        hasText(request.getMelding(), "melding");
    }

}
