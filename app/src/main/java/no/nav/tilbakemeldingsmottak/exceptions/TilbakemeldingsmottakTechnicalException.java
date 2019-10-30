package no.nav.tilbakemeldingsmottak.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class TilbakemeldingsmottakTechnicalException extends RuntimeException {
	private final HttpStatus httpStatus;

	public TilbakemeldingsmottakTechnicalException(String message) {
		super(message);
		this.httpStatus = null;
	}

	public TilbakemeldingsmottakTechnicalException(HttpStatus httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public TilbakemeldingsmottakTechnicalException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public TilbakemeldingsmottakTechnicalException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}
	public TilbakemeldingsmottakTechnicalException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = null;
	}

}
