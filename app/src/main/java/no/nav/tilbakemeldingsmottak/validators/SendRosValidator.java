package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.api.SendRosRequest;

public class SendRosValidator extends AbstractValidator {

    public void validateRequest(SendRosRequest request) {
        hasText(request.getNavn(), "navn");
        hasText(request.getTelefonnummer(), "telefonnummer");
        hasText(request.getHvemRoses(), "hvemRoses");
        hasText(request.getMelding(), "melding");
    }

}
