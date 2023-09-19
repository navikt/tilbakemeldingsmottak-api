package no.nav.tilbakemeldingsmottak.exceptions

/**
 * Kastes dersom kall til opprettJournalpost eller opprettOppgave feiler.
 * Tjenesten skal håndtere dette og returnere 200 OK til sluttbruker.
 */
open class EksterntKallException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode = ErrorCode.GENERAL_ERROR
) : RuntimeException(message)