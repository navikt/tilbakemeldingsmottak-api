package no.nav.tilbakemeldingsmottak.exceptions.norg2;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HentEnheterFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
    public HentEnheterFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
