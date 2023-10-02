package no.nav.tilbakemeldingsmottak.validators

import no.nav.tilbakemeldingsmottak.TestUtils.TestUtils.createHentSkjemaResponse
import no.nav.tilbakemeldingsmottak.TestUtils.TestUtils.createHentSkjemaResponseWithDefaultAnswers
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_MUNTLIG_ANSWER
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator
import no.nav.tilbakemeldingsmottak.util.builders.KlassifiserServiceklageRequestBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class KlassifiserServiceklageValidatorTest {
    private val klassifiserServiceklageValidator = KlassifiserServiceklageValidator()
    private var klassifiserServiceklageRequest: KlassifiserServiceklageRequest =
        KlassifiserServiceklageRequestBuilder().build()

    @Test
    fun happyPathServiceklage() {
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build()
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse())
    }

    @Test
    fun happyPathAnnet() {
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().asNotServiceklage().build()
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse())
    }

    @Test
    fun happyPathForvaltningsklage() {
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().asForvaltningsklage().build()
        klassifiserServiceklageValidator.validateRequest(klassifiserServiceklageRequest, createHentSkjemaResponse())
    }

    @Test
    fun shouldThrowExceptionIfChoiceNotValid() {
        // Given
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build(TEMA = "Ugyldig valg")

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            klassifiserServiceklageValidator.validateRequest(
                klassifiserServiceklageRequest,
                createHentSkjemaResponse()
            )
        }

        // Then
        assertTrue(thrown.message!!.contains("Innsendt svar på spørsmål med id=TEMA er ikke gyldig"))
    }

    @Test
    fun shouldThrowExceptionIfTextIsBlank() {
        // Given
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build(AARSAK = "")

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            klassifiserServiceklageValidator.validateRequest(
                klassifiserServiceklageRequest,
                createHentSkjemaResponse()
            )
        }

        // Then
        assertTrue(thrown.message!!.contains("Innsendt svar på spørsmål med id=AARSAK er ikke gyldig"))
    }

    @Test
    fun shouldThrowExceptionIfDateIsInvalid() {
        // Given
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build(FREMMET_DATO = "123")

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            klassifiserServiceklageValidator.validateRequest(
                klassifiserServiceklageRequest,
                createHentSkjemaResponse()
            )
        }

        // Then
        assertTrue(thrown.message!!.contains("Innsendt svar på spørsmål med id=FREMMET_DATO er ikke gyldig"))
    }

    @Test
    fun shouldThrowExceptionIfDefaultAnswerDoesntMatch() {
        // Given
        klassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build(KANAL = KANAL_MUNTLIG_ANSWER)
        val hentSkjemaResponse: HentSkjemaResponse = createHentSkjemaResponseWithDefaultAnswers()

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            klassifiserServiceklageValidator.validateRequest(
                klassifiserServiceklageRequest,
                hentSkjemaResponse
            )
        }

        // Then
        assertTrue(thrown.message!!.contains("Innsendt svar på spørsmål med id=KANAL matcher ikke svar i database"))
    }
}