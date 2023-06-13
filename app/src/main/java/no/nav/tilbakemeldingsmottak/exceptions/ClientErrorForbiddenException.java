package no.nav.tilbakemeldingsmottak.exceptions;

public class ClientErrorForbiddenException extends AbstractGeneralException {
    public ClientErrorForbiddenException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

}
