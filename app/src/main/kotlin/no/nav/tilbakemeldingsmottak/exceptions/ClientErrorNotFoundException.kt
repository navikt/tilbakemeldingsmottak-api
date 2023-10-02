package no.nav.tilbakemeldingsmottak.exceptions

// Errors som skal gi 404 respons
open class ClientErrorNotFoundException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode = ErrorCode.GENERAL_ERROR
) : RuntimeException(message)