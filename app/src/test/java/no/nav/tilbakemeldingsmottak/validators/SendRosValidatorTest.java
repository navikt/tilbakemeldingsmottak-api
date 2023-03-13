package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator;
import org.junit.jupiter.api.Test;

class SendRosValidatorTest {

    private SendRosValidator sendRosValidator = new SendRosValidator();
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
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("hvemRoses er påkrevd"));
    }

    @Test
    void shouldThrowExceptionIfKontorNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(SendRosRequest.HvemRosesEnum.KONTOR);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("navKontor er påkrevd dersom hvemRoses=NAV_KONTOR"));
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