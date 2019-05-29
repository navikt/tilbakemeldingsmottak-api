package no.nav.serviceklagemottak.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public final class ServiceklageIkkeFunnetException extends ServiceklagemottakFunctionalException {
	public ServiceklageIkkeFunnetException() {
		super();
	}

	public ServiceklageIkkeFunnetException(String message) {
		super(message);
	}

	public ServiceklageIkkeFunnetException(String message, Exception e) {
		super(message, e);
	}
}
