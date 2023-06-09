package no.nav.tilbakemeldingsmottak.exceptions;

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
