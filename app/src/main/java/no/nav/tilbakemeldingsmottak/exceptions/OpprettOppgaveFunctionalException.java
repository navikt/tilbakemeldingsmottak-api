package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class OpprettOppgaveFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
    public OpprettOppgaveFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
