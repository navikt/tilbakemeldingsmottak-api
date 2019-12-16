package no.nav.tilbakemeldingsmottak.exceptions.oppgave;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class EndreOppgaveTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
    public EndreOppgaveTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
