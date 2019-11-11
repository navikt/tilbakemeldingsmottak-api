package no.nav.tilbakemeldingsmottak.rest.common.validation;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;

public abstract class RequestValidator {

    protected void hasText(String input, String feltnavn) {
        hasText(input, feltnavn, "");
    }

    protected void hasText(String input, String feltnavn, String condition) {
        if (StringUtils.isBlank(input)) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }

    protected void isNotNull(Object input, String feltnavn) {
        isNotNull(input, feltnavn, "");
    }

    protected void isNotNull(Object input, String feltnavn, String condition) {
        if (input == null) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }

    protected void isNull(Object input, String feltnavn) {
        isNull(input, feltnavn, "");
    }

    protected void isNull(Object input, String feltnavn, String condition) {
        if (input != null) {
            throw new InvalidRequestException(String.format("%s kan ikke være satt%s", feltnavn, condition));
        }
    }
}
