package no.nav.tilbakemeldingsmottak.exceptions;

public class PdfException extends AbstractTilbakemeldingsmottakTechnicalException {
    public PdfException(String message) {
        super(message);
    }

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }
}
