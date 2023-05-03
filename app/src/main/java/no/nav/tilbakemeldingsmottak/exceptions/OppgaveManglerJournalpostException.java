package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class OppgaveManglerJournalpostException extends AbstractTilbakemeldingsmottakFunctionalException {

    public OppgaveManglerJournalpostException(String message) {
        super(message);
    }

}
