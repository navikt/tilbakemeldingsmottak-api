package no.nav.tilbakemeldingsmottak.exceptions;

// Errors som skal gi 204 respons
public class NoContentException extends AbstractGeneralException {
    public NoContentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
