package no.nav.tilbakemeldingsmottak.exceptions;

/**
 * Kastes dersom kall til opprettJournalpost eller opprettOppgave feiler.
 * Tjenesten skal håndtere dette og returnere 200 OK til sluttbruker.
 */
public class EksterntKallException extends AbstractTilbakemeldingsmottakTechnicalException {

    public EksterntKallException(String message) {
        super(message);
    }
}
