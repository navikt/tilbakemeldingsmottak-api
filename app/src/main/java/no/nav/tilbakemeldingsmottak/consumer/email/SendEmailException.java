package no.nav.tilbakemeldingsmottak.consumer.email;

public class SendEmailException extends RuntimeException {

    public SendEmailException(String message) {
        super(message);
    }
}
