package no.nav.tilbakemeldingsmottak.exceptions;

public class ClientErrorForbiddenException extends AbstractGeneralException {
    public ClientErrorForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientErrorForbiddenException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ClientErrorForbiddenException(String message) {
        super(message);
    }

    public ClientErrorForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
