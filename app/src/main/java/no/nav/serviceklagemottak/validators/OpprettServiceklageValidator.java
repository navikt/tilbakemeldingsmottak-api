package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import org.springframework.stereotype.Component;

@Component
public class OpprettServiceklageValidator extends AbstractValidator {

    public void validateRequest(OpprettServiceklageRequest request) {
        validateEmail(request.getEmail());
        hasText(request.getKlagetekst(), "klagetekst");
    }

}
