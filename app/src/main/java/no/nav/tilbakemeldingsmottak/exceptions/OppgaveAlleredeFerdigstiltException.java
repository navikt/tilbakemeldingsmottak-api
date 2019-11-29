package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OppgaveAlleredeFerdigstiltException extends AbstractTilbakemeldingsmottakFunctionalException {

	public OppgaveAlleredeFerdigstiltException(String message) {
		super(message);
	}
}
