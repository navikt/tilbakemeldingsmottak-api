package no.nav.tilbakemeldingsmottak.exceptions;

public class ClientErrorUnauthorizedException extends AbstractGeneralException {

    public ClientErrorUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientErrorUnauthorizedException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
