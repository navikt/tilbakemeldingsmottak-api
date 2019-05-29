package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.api.RegistrerTilbakemeldingRequest;
import org.springframework.stereotype.Component;

@Component
public class RegistrerTilbakemeldingValidator extends AbstractValidator {

    public void validateRequest(RegistrerTilbakemeldingRequest request) {
        hasText(request.getTilbakemelding(), "tilbakemelding");
    }

}
