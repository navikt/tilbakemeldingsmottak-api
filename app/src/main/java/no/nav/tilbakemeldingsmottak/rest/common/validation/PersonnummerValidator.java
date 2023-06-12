package no.nav.tilbakemeldingsmottak.rest.common.validation;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import org.springframework.stereotype.Component;

import static java.lang.Character.getNumericValue;
import static org.springframework.util.Assert.notNull;

@Component
@RequiredArgsConstructor
public class PersonnummerValidator {

    private static final int[] CONTROL_DIGIT_C1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] CONTROL_DIGIT_C2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int INVALID_CONTROL_DIGIT = 10;
    private static final int DEFAULT_MODULUS = 11;
    private static final int C1_POS = 9;
    private static final int C2_POS = 10;

    private static final String FEILMELDING = "Feil i validering av personnummer";

    public void validate(String ident) {
        notNull(ident, "Personidentifikator kan ikke være null");

        if (ident.matches("\\d{11}")) {
            validateControlDigits(ident);
        } else {
            throw new ClientErrorException(FEILMELDING);
        }
    }

    private int calculateControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            digitsum += getNumericValue(fnr.charAt(i)) * sequence[i];
        }
        digitsum = DEFAULT_MODULUS - (digitsum % DEFAULT_MODULUS);

        return digitsum == DEFAULT_MODULUS ? 0 : digitsum;
    }

    private void validateControlDigits(String ident) {
        int c1 = getNumericValue(ident.charAt(C1_POS));
        int c2 = getNumericValue(ident.charAt(C2_POS));
        int calcC1 = calculateControlDigit(ident, CONTROL_DIGIT_C1);
        int calcC2 = calculateControlDigit(ident, CONTROL_DIGIT_C2);

        if (calcC1 == INVALID_CONTROL_DIGIT || calcC2 == INVALID_CONTROL_DIGIT) {
            throw new ClientErrorException(FEILMELDING);
        }

        if (c1 != calcC1 || c2 != calcC2) {
            throw new ClientErrorException(FEILMELDING);
        }
    }

}