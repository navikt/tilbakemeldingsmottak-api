package no.nav.tilbakemeldingsmottak.exceptions;

// Errors som skal gi 400 respons
public class ClientErrorException extends AbstractGeneralException {

    public ClientErrorException(String message) {
        super(message);
    }

    public ClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientErrorException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ClientErrorException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
