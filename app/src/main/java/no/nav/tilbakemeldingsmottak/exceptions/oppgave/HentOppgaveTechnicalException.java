package no.nav.tilbakemeldingsmottak.exceptions.oppgave;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class HentOppgaveTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
    public HentOppgaveTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
