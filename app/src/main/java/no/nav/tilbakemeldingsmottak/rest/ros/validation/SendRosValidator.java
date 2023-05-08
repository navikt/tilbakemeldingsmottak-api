package no.nav.tilbakemeldingsmottak.rest.ros.validation;

import no.nav.tilbakemeldingsmottak.model.SendRosRequest;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest.HvemRosesEnum;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import org.springframework.stereotype.Component;

@Component
public class SendRosValidator extends RequestValidator {

    public void validateRequest(SendRosRequest request) {
        isNotNull(request.getHvemRoses(), "hvemRoses");
        if (HvemRosesEnum.NAV_KONTOR.equals(request.getHvemRoses())) {
            hasText(request.getNavKontor(), "navKontor", " dersom hvemRoses=NAV_KONTOR");
        }
        hasText(request.getMelding(), "melding");
    }

}
