package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;

public class MeldFeilOgManglerValidator extends AbstractValidator {

    public void validateRequest(MeldFeilOgManglerRequest request) {
        hasText(request.getNavn(), "navn");
        hasText(request.getTelefonnummer(), "telefonnummer");
        hasText(request.getFeiltype(), "feiltype");
        hasText(request.getMelding(), "melding");
    }

}
