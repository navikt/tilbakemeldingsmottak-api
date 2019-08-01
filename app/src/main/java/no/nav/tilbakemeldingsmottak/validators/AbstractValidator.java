package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator {

    static void hasText(String input, String feltnavn) {
        hasText(input, feltnavn, "");
    }

    static void hasText(String input, String feltnavn, String condition) {
        if (StringUtils.isBlank(input)) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }

    static void isNotNull(Object input, String feltnavn) {
        isNotNull(input, feltnavn, "");
    }

    static void isNotNull(Object input, String feltnavn, String condition) {
        if (input == null) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }
}
