package no.nav.tilbakemeldingsmottak.exceptions.pdl;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class PdlGraphqlException extends AbstractTilbakemeldingsmottakTechnicalException {
    public PdlGraphqlException(String message, Throwable cause) {
        super(message, cause);
    }
}

