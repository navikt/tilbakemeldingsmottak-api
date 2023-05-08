package no.nav.tilbakemeldingsmottak.exceptions.saf;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class SafHentDokumentTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
    public SafHentDokumentTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
