package no.nav.tilbakemeldingsmottak.exceptions;

// Errors som skal gi 500 respons
public class ServerErrorException extends AbstractGeneralException {
    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public ServerErrorException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
