package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends AbstractTilbakemeldingsmottakFunctionalException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
