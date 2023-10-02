package no.nav.tilbakemeldingsmottak.exceptions

// Errors som skal gi 400 respons
open class ClientErrorException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode = ErrorCode.GENERAL_ERROR
) : RuntimeException(message)
