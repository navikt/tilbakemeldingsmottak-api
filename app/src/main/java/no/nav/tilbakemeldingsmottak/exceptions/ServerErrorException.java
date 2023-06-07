package no.nav.tilbakemeldingsmottak.exceptions;

public class ServerErrorException extends AbstractGeneralException {
    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerErrorException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
