package no.nav.tilbakemeldingsmottak.exceptions.saf;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SafJournalpostIkkeFunnetFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {

    public SafJournalpostIkkeFunnetFunctionalException(String message) {
        super(message);
    }
}
