package no.nav.tilbakemeldingsmottak.rest.common.validation;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public interface RequestValidator {

    default void hasText(String input, String feltnavn) {
        hasText(input, feltnavn, "");
    }

    default void hasText(String input, String feltnavn, String condition) {
        if (StringUtils.isBlank(input)) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }

    default void isNotNull(Object input, String feltnavn) {
        isNotNull(input, feltnavn, "");
    }

    default void isNotNull(Object input, String feltnavn, String condition) {
        if (input == null) {
            throw new InvalidRequestException(String.format("%s er påkrevd%s", feltnavn, condition));
        }
    }

    default void isNull(Object input, String feltnavn) {
        isNull(input, feltnavn, "");
    }

    default void isNull(Object input, String feltnavn, String condition) {
        if (input != null) {
            throw new InvalidRequestException(String.format("%s kan ikke være satt%s", feltnavn, condition));
        }
    }


}
