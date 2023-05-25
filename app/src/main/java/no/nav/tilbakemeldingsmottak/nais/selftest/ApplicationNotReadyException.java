package no.nav.tilbakemeldingsmottak.nais.selftest;

public class ApplicationNotReadyException extends RuntimeException {
    public ApplicationNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }
}