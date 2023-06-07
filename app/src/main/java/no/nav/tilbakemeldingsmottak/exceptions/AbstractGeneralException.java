package no.nav.tilbakemeldingsmottak.exceptions;

// Message er for utviklere
// ErrorCode er for frontend som skal mappe koden til en brukervennlig tekst
public abstract class AbstractGeneralException extends RuntimeException {
    private ErrorCode errorCode;

    public AbstractGeneralException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }

    public AbstractGeneralException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
