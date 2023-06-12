package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator;
import org.junit.jupiter.api.Test;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendRosValidatorTest {

    private final SendRosValidator sendRosValidator = new SendRosValidator();
    private SendRosRequest sendRosRequest;

    @Test
    void happyPath() {
        sendRosRequest = createSendRosRequest();
        sendRosValidator.validateRequest(sendRosRequest);
    }

    @Test
    void shouldThrowExceptionIfHvemRosesNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(null);
        Exception thrown = assertThrows(ClientErrorException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("hvemRoses er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfKontorNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(SendRosRequest.HvemRosesEnum.NAV_KONTOR);
        Exception thrown = assertThrows(ClientErrorException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("navKontor er påkrevd dersom hvemRoses=NAV_KONTOR"));
    }

    @Test
    void shouldThrowExceptionIfMeldingNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setMelding(null);
        Exception thrown = assertThrows(ClientErrorException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("melding er påkrevd"));
    }

}