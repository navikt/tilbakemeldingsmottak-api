package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class OpprettJournalpostFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
    public OpprettJournalpostFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
