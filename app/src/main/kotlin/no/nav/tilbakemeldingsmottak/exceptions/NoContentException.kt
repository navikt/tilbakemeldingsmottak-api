package no.nav.tilbakemeldingsmottak.exceptions

// Errors som skal gi 204 respons
open class NoContentException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode = ErrorCode.GENERAL_ERROR
) : RuntimeException(message)