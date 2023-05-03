package no.nav.tilbakemeldingsmottak.exceptions;

public abstract class AbstractTilbakemeldingsmottakFunctionalException extends RuntimeException {

    public AbstractTilbakemeldingsmottakFunctionalException(String message) {
        super(message);
    }

    public AbstractTilbakemeldingsmottakFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

}
