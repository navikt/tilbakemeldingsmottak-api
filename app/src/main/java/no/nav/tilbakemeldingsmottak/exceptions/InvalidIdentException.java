package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidIdentException extends AbstractTilbakemeldingsmottakFunctionalException {

    public InvalidIdentException(String message) {
        super(message);
    }
}
