package no.nav.tilbakemeldingsmottak.validators;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator;
import org.junit.Test;

public class SendRosValidatorTest {

    private SendRosValidator sendRosValidator = new SendRosValidator();
    private SendRosRequest sendRosRequest;

    @Test
    public void happyPath() {
        sendRosRequest = createSendRosRequest();
        sendRosValidator.validateRequest(sendRosRequest);
    }

    @Test
    public void shouldThrowExceptionIfHvemRosesNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("hvemRoses er påkrevd"));
    }

    @Test
    public void shouldThrowExceptionIfKontorNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setHvemRoses(HvemRosesType.NAV_KONTOR);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("navKontor er påkrevd dersom hvemRoses=NAV_KONTOR"));
    }

    @Test
    public void shouldThrowExceptionIfMeldingNotSet() {
        sendRosRequest = createSendRosRequest();
        sendRosRequest.setMelding(null);
        Exception thrown = assertThrows(InvalidRequestException.class,
                () -> sendRosValidator.validateRequest(sendRosRequest));
        assertTrue(thrown.getMessage().contains("melding er påkrevd"));
    }

}