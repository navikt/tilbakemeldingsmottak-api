package no.nav.tilbakemeldingsmottak.exceptions

// Errors som skal gi 500 respons
open class ServerErrorException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode = ErrorCode.GENERAL_ERROR
) : RuntimeException(message)
