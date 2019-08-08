package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.api.HvemRosesType.NAV_KONTOR;

import no.nav.tilbakemeldingsmottak.api.SendRosRequest;

public class SendRosValidator implements RequestValidator {

    public void validateRequest(SendRosRequest request) {
        hasText(request.getNavn(), "navn");
        hasText(request.getTelefonnummer(), "telefonnummer");
        isNotNull(request.getHvemRoses(), "hvemRoses");
        if(NAV_KONTOR.equals(request.getHvemRoses())) {
            hasText(request.getNavKontor(), "hvemRoses", "dersom hvemRoses=NAV_KONTOR");
        }
        hasText(request.getMelding(), "melding");
    }

}
