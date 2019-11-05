package no.nav.tilbakemeldingsmottak.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class TilbakemeldingsmottakTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
	private final HttpStatus httpStatus;

	public TilbakemeldingsmottakTechnicalException(String message) {
		super(message);
		this.httpStatus = null;
	}

	public TilbakemeldingsmottakTechnicalException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = null;
	}

}
