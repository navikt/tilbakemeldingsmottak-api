package no.nav.tilbakemeldingsmottak.exceptions.oppgave;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EndreOppgaveFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
    public EndreOppgaveFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
