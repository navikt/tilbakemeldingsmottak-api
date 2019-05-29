package no.nav.serviceklagemottak.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ServiceklagemottakTechnicalException extends RuntimeException {
	private final HttpStatus httpStatus;

	public ServiceklagemottakTechnicalException(HttpStatus httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public ServiceklagemottakTechnicalException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public ServiceklagemottakTechnicalException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}
	public ServiceklagemottakTechnicalException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = null;
	}

}
