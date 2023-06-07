package no.nav.tilbakemeldingsmottak.exceptions;

public class ClientErrorException extends AbstractGeneralException {
    public ClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientErrorException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
