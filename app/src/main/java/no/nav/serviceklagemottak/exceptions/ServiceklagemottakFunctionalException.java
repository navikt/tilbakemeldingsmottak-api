package no.nav.serviceklagemottak.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ServiceklagemottakFunctionalException extends RuntimeException {
	private final HttpStatus httpStatus;

	public ServiceklagemottakFunctionalException() {
		super();
		httpStatus = null;
	}

	public ServiceklagemottakFunctionalException(String message) {
		super(message);
		this.httpStatus = null;
	}

	public ServiceklagemottakFunctionalException(HttpStatus httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public ServiceklagemottakFunctionalException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public ServiceklagemottakFunctionalException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public ServiceklagemottakFunctionalException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = null;
	}
}
