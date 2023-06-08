package no.nav.tilbakemeldingsmottak.exceptions;

// Errors som skal gi 403 respons
public class ClientErrorUnauthorizedException extends AbstractGeneralException {

    public ClientErrorUnauthorizedException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
