package no.nav.tilbakemeldingsmottak.exceptions;

// Errors som skal gi 404 respons
public class ClientErrorNotFoundException extends AbstractGeneralException {
    public ClientErrorNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
