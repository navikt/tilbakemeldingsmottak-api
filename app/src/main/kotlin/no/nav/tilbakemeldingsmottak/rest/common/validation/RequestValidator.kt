package no.nav.tilbakemeldingsmottak.rest.common.validation

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import org.apache.commons.lang3.StringUtils

abstract class RequestValidator {
    protected fun hasText(input: String?, feltnavn: String?, condition: String? = "") {
        if (StringUtils.isBlank(input)) {
            throw ClientErrorException(String.format("%s er påkrevd%s", feltnavn, condition))
        }
    }

    protected fun isNotNull(input: Any?, feltnavn: String?) {
        isNotNull(input, feltnavn, "")
    }

    protected fun isNotNull(input: Any?, feltnavn: String?, condition: String?) {
        if (input == null) {
            throw ClientErrorException(String.format("%s er påkrevd%s", feltnavn, condition))
        }
    }

    protected fun isNull(input: Any?, feltnavn: String?) {
        isNull(input, feltnavn, "")
    }

    protected fun isNull(input: Any?, feltnavn: String?, condition: String?) {
        if (input != null) {
            throw ClientErrorException(String.format("%s kan ikke være satt%s", feltnavn, condition))
        }
    }
}
