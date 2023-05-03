package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OidcContextException extends AbstractTilbakemeldingsmottakFunctionalException {

    public OidcContextException(String message) {
        super(message);
    }
}
