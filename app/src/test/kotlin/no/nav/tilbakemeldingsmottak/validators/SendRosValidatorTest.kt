package no.nav.tilbakemeldingsmottak.validators

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosRequest.HvemRoses
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator
import no.nav.tilbakemeldingsmottak.util.builders.SendRosRequestBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SendRosValidatorTest {
    private val sendRosValidator = SendRosValidator()
    private var sendRosRequest: SendRosRequest = SendRosRequestBuilder().build()

    @Test
    fun happyPath() {
        sendRosRequest = SendRosRequestBuilder().build()
        sendRosValidator.validateRequest(sendRosRequest)
    }

    @Test
    fun shouldThrowExceptionIfHvemRosesNotSet() {
        // Given
        sendRosRequest = SendRosRequestBuilder().build(hvemRoses = null)

        // When
        val thrown =
            Assertions.assertThrows(ClientErrorException::class.java) { sendRosValidator.validateRequest(sendRosRequest) }

        // Then
        assertTrue(thrown.message.contains("hvemRoses er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfKontorNotSet() {
        // Given
        sendRosRequest = SendRosRequestBuilder().build(hvemRoses = HvemRoses.NAV_KONTOR)

        // When
        val thrown =
            Assertions.assertThrows(ClientErrorException::class.java) { sendRosValidator.validateRequest(sendRosRequest) }

        // Then
        assertTrue(thrown.message.contains("navKontor er påkrevd dersom hvemRoses=NAV_KONTOR"))
    }

    @Test
    fun shouldThrowExceptionIfMeldingNotSet() {
        // Given
        sendRosRequest = SendRosRequestBuilder().build(melding = null)

        // When
        val thrown =
            Assertions.assertThrows(ClientErrorException::class.java) { sendRosValidator.validateRequest(sendRosRequest) }

        // Then
        assertTrue(thrown.message.contains("melding er påkrevd"))
    }
}