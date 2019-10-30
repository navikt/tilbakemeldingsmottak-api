package no.nav.tilbakemeldingsmottak.exceptions.saf;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidMappingToEnumFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {
	public InvalidMappingToEnumFunctionalException(String message) {
		super(message);
	}
}
