package no.nav.tilbakemeldingsmottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public final class GyldigDokumentIkkeFunnetException extends TilbakemeldingsmottakFunctionalException {
	public GyldigDokumentIkkeFunnetException() {
		super();
	}

	public GyldigDokumentIkkeFunnetException(String message) {
		super(message);
	}

	public GyldigDokumentIkkeFunnetException(String message, Exception e) {
		super(message, e);
	}
}
