package no.nav.tilbakemeldingsmottak.exceptions;

public abstract class AbstractTilbakemeldingsmottakTechnicalException extends RuntimeException {

    public AbstractTilbakemeldingsmottakTechnicalException(String message) {
        super(message);
    }

    public AbstractTilbakemeldingsmottakTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
