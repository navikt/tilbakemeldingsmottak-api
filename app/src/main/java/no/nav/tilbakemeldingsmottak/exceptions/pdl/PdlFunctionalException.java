package no.nav.tilbakemeldingsmottak.exceptions.pdl;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;

public class PdlFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
    public PdlFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}

