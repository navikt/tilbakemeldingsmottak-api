package no.nav.tilbakemeldingsmottak.exceptions.saf;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class SafJournalpostQueryUnauthorizedException extends AbstractTilbakemeldingsmottakFunctionalException {

    public SafJournalpostQueryUnauthorizedException(String message) {
        super(message);
    }

    public SafJournalpostQueryUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
