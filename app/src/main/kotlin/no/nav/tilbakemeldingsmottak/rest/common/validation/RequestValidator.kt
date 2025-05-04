package no.nav.tilbakemeldingsmottak.rest.common.validation

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import org.apache.commons.lang3.StringUtils

abstract class RequestValidator {

    private val illegalCharRegex = Regex("[^\\p{L}\\p{N}@._+\\- ':!,\\u00C0-\\u017F]")

    protected fun hasText(input: String?, feltnavn: String?, condition: String? = "") {
        if (StringUtils.isBlank(input)) {
            throw ClientErrorException(String.format("%s er påkrevd%s", feltnavn, condition))
        }
        isValidInput(input, feltnavn)
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

    protected fun isValidInput(input: String?, feltnavn: String?) {
        if (input.isNullOrEmpty()) {
            return
        }
        val illegalCharacters = findIllegalCharacters(input)
        if (illegalCharacters.isNotEmpty()) {
            throw ClientErrorException(
                String.format(
                    "%s inneholder ulovlige karakterer %s",
                    feltnavn,
                    illegalCharacters.joinToString(", ")
                )
            )
        }
    }

    private fun findIllegalCharacters(input: String?): List<Char> {
        if (input.isNullOrEmpty()) {
            return emptyList()
        }
        return illegalCharRegex.findAll(input)
            .map { it.value.first() }
            .distinct()
            .toList()
    }
}
