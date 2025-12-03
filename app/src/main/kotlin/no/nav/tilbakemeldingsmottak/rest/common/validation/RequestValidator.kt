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

    protected fun isLegalTelephoneNumber(input: String?, feltnavn: String?, condition: String? = "") {
        isNotNull(input, feltnavn)
        val regex = Regex("^([0-9() +-]*)$")
        if (regex.matchEntire(input!!) == null) {
            throw ClientErrorException(String.format("%s inneholder ulovlige karakterer%s", feltnavn, condition))
        }

    }

    protected fun maxSize(input: String?, maxSize: Int, feltnavn: String?) {
        isNotNull(input, feltnavn)
        if ((input?.length ?: 0) > maxSize) {
            throw ClientErrorException(String.format("%s inneholder for lang tekst%s", feltnavn, null))
        }
    }

    protected fun isLegalEmail(input: String?, feltnavn: String?, condition: String? = "") {
        isNotNull(input, feltnavn)
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        if (!input!!.matches(emailRegex.toRegex())) {
            throw ClientErrorException(String.format("%s er ikke en gyldig epost adresse%s", feltnavn, condition))
        }
    }
}
