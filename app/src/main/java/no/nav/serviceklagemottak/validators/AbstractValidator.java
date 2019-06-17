package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator {

    static void hasText(String input, String feltnavn) {
        hasText(input, feltnavn, "");
    }

    static void hasText(String input, String feltnavn, String condition) {
        if (StringUtils.isBlank(input)) {
            throw new InvalidRequestException(String.format("%s kan ikke være null eller tom %s", feltnavn, condition));
        }
    }

    static void isNotNull(Object input, String feltnavn) {
        isNotNull(input, feltnavn, "");
    }

    static void isNotNull(Object input, String feltnavn, String condition) {
        if (input == null) {
            throw new InvalidRequestException(String.format("%s kan ikke være null %s", feltnavn, condition));
        }
    }
}
