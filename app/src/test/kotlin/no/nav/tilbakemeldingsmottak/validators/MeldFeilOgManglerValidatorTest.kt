package no.nav.tilbakemeldingsmottak.validators

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator
import no.nav.tilbakemeldingsmottak.util.builders.MeldFeilOgManglerRequestBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MeldFeilOgManglerValidatorTest {
    private val meldFeilOgManglerValidator = MeldFeilOgManglerValidator()
    private var meldFeilOgManglerRequest: MeldFeilOgManglerRequest = MeldFeilOgManglerRequestBuilder().build()

    @Test
    fun happyPath() {
        meldFeilOgManglerRequest = MeldFeilOgManglerRequestBuilder().build()
        meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
    }

    @Test
    fun shouldThrowExceptionIfOnskerKontaktTrueAndEpostNotSet() {
        // Given
        meldFeilOgManglerRequest = MeldFeilOgManglerRequestBuilder().build(onskerKontakt = true, epost = null)

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
        }

        // Then
        assertTrue(thrown.message!!.contains("epost er påkrevd dersom onskerKontakt=true"))
    }

    @Test
    fun shouldThrowExceptionIfFeiltypeNotSet() {
        // Given
        meldFeilOgManglerRequest = MeldFeilOgManglerRequestBuilder().build(feiltype = null)

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
        }

        // Then
        assertTrue(thrown.message!!.contains("feiltype er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfMeldingNotSet() {
        // Given
        meldFeilOgManglerRequest = MeldFeilOgManglerRequestBuilder().build(melding = null)

        // When
        val thrown: Exception = Assertions.assertThrows(ClientErrorException::class.java) {
            meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
        }

        // Then
        assertTrue(thrown.message!!.contains("melding er påkrevd"))
    }
}