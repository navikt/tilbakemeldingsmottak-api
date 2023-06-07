package no.nav.tilbakemeldingsmottak.exceptions;

public enum ErrorCode {
    GENERAL_ERROR("GENERAL_ERROR"),

    // EREG
    EREG_UNAUTHORIZED("EREG_UNAUTHORIZED"), // mangler tilgang til å hente data fra ereg
    EREG_ERROR("EREG_ERROR"); // validering av organisasjonsnummer feiler


    public final String value;

    ErrorCode(String value) {
        this.value = value;
    }
}
