package no.nav.tilbakemeldingsmottak.exceptions;

/**
 * Kastes dersom kall til opprettJournalpost eller opprettOppgave feiler.
 * Tjenesten skal håndtere dette og returnere 200 OK til sluttbruker.
 */
public class EksterntKallException extends AbstractGeneralException {

    public EksterntKallException(String message, Throwable cause) {
        super(message, cause);
    }

    public EksterntKallException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
