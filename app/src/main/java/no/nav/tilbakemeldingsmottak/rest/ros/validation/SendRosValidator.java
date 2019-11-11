package no.nav.tilbakemeldingsmottak.rest.ros.validation;

import static no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType.NAV_KONTOR;

import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import org.springframework.stereotype.Component;

@Component
public class SendRosValidator extends RequestValidator {

    public void validateRequest(SendRosRequest request) {
        isNotNull(request.getHvemRoses(), "hvemRoses");
        if(NAV_KONTOR.equals(request.getHvemRoses())) {
            hasText(request.getNavKontor(), "navKontor", " dersom hvemRoses=NAV_KONTOR");
        }
        hasText(request.getMelding(), "melding");
    }

}
