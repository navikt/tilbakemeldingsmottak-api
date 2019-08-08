package no.nav.tilbakemeldingsmottak.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class TilbakemeldingsmottakFunctionalException extends RuntimeException {
	private final HttpStatus httpStatus;

	public TilbakemeldingsmottakFunctionalException() {
		super();
		httpStatus = null;
	}

	public TilbakemeldingsmottakFunctionalException(String message) {
		super(message);
		this.httpStatus = null;
	}

	public TilbakemeldingsmottakFunctionalException(HttpStatus httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public TilbakemeldingsmottakFunctionalException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public TilbakemeldingsmottakFunctionalException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public TilbakemeldingsmottakFunctionalException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = null;
	}
}
