package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator {

    private static final String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    protected void validateEmail(String email) {
        if (!email.matches(emailRegex)) {
            throw new InvalidRequestException("Email-adresse er ikke på riktig format.");
        }
    }

    protected static void hasText(String input, String feltnavn) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(String.format("%s kan ikke være null eller tom", feltnavn));
        }
    }

}
