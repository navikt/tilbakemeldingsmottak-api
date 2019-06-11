package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractValidator {

    protected static void hasText(String input, String feltnavn) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(String.format("%s kan ikke være null eller tom", feltnavn));
        }
    }

}
