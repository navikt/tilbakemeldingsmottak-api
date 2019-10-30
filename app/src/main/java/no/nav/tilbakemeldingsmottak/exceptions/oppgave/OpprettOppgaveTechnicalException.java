package no.nav.tilbakemeldingsmottak.exceptions.oppgave;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class OpprettOppgaveTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
    public OpprettOppgaveTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
