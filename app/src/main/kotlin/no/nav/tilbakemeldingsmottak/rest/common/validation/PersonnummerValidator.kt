package no.nav.tilbakemeldingsmottak.rest.common.validation

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class PersonnummerValidator {

    private val CONTROL_DIGIT_C1 = intArrayOf(3, 7, 6, 1, 8, 9, 4, 5, 2)
    private val CONTROL_DIGIT_C2 = intArrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)
    private val INVALID_CONTROL_DIGIT = 10
    private val DEFAULT_MODULUS = 11
    private val C1_POS = 9
    private val C2_POS = 10
    private val FEILMELDING = "Feil i validering av personnummer"
    
    fun validate(ident: String) {
        Assert.notNull(ident, "Personidentifikator kan ikke være null")
        if (ident.matches("\\d{11}".toRegex())) {
            validateControlDigits(ident)
        } else {
            throw ClientErrorException(FEILMELDING)
        }
    }

    private fun calculateControlDigit(fnr: String, vararg sequence: Int): Int {
        var digitsum = 0
        for (i in sequence.indices) {
            digitsum += Character.getNumericValue(fnr[i]) * sequence[i]
        }
        digitsum = DEFAULT_MODULUS - digitsum % DEFAULT_MODULUS
        return if (digitsum == DEFAULT_MODULUS) 0 else digitsum
    }

    private fun validateControlDigits(ident: String) {
        val c1 = Character.getNumericValue(ident[C1_POS])
        val c2 = Character.getNumericValue(ident[C2_POS])
        val calcC1 = calculateControlDigit(ident, *CONTROL_DIGIT_C1)
        val calcC2 = calculateControlDigit(ident, *CONTROL_DIGIT_C2)
        if (calcC1 == INVALID_CONTROL_DIGIT || calcC2 == INVALID_CONTROL_DIGIT) {
            throw ClientErrorException(FEILMELDING)
        }
        if (c1 != calcC1 || c2 != calcC2) {
            throw ClientErrorException(FEILMELDING)
        }
    }

}