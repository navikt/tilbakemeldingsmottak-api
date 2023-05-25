package no.nav.tilbakemeldingsmottak.exceptions.ereg;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class EregTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {

    public EregTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
