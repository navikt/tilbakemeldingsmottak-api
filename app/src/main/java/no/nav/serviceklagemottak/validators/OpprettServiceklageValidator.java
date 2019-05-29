package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class OpprettServiceklageValidator {

    private static final String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public void validateRequest(OpprettServiceklageRequest request) {
        validateEmail(request.getEmail());
    }

    private void validateEmail(String email) {
        if (!email.matches(emailRegex)) {
            throw new InvalidRequestException("Email-adresse er ikke på riktig format.");
        }
    }

}
