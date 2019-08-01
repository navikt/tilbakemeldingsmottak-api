package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.api.SendRosRequest;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

class SendRosValidatorTest {
    private SendRosRequest sendRosRequest;
    private SendRosValidator sendRosValidator = new SendRosValidator();

    @Test
    void happyPath() {
        sendRosRequest = createSendRosRequest();
        sendRosValidator.validateRequest(sendRosRequest);
    }

    @Test
    void shouldThrowExceptionIfNavnNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setNavn(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("navn er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfTelefonnummerNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setTelefonnummer(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("telefonnummer er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfHvemRosesNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("hvemRoses er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfMeldingNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setMelding(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("melding er påkrevd"));
    }

}