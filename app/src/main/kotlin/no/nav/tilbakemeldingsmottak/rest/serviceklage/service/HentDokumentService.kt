package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import no.nav.tilbakemeldingsmottak.consumer.saf.SafJournalpostQueryService
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentConsumer
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.NoContentException
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse
import no.nav.tilbakemeldingsmottak.saf.generated.enums.Variantformat
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.DokumentInfo
import org.springframework.stereotype.Service

@Service
class HentDokumentService(
    private val safJournalpostQueryService: SafJournalpostQueryService,
    private val hentDokumentConsumer: HentDokumentConsumer,
) {
    fun hentDokument(journalpostId: String): HentDokumentResponse {
        //val authorizationHeader = "Bearer ${oidcUtils.getFirstValidToken()}"
        val journalpost = safJournalpostQueryService.hentJournalpost(journalpostId)

        val variantformat: Variantformat
        val dokumentInfo: DokumentInfo
        if (journalpost.dokumenter == null || journalpost.dokumenter.isEmpty() || journalpost.dokumenter.get(0) == null) {
            throw NoContentException(
                message = "Fant ingen dokument på journalpost $journalpostId",
                errorCode = ErrorCode.JOURNALPOST_MISSING_DOKUMENT
            )
        } else {
            dokumentInfo = journalpost.dokumenter[0]!!
            variantformat = when {
                dokumentInfo.dokumentvarianter.isEmpty() -> throw NoContentException(
                    message = "Fant ingen tilgjengelig dokument på journalpost $journalpostId",
                    errorCode = ErrorCode.JOURNALPOST_MISSING_DOKUMENT
                )

                dokumentInfo.dokumentvarianter.any { it!!.variantformat == Variantformat.SLADDET } -> Variantformat.SLADDET
                dokumentInfo.dokumentvarianter.any { it!!.variantformat == Variantformat.ARKIV } -> Variantformat.ARKIV
                else -> throw NoContentException(
                    message = "Fant ingen tilgjengelig dokument på journalpost $journalpostId",
                    errorCode = ErrorCode.JOURNALPOST_MISSING_DOKUMENT
                )
            }
        }
        val safHentDokumentResponseTo = hentDokumentConsumer.hentDokument(
            journalpostId,
            dokumentInfo.dokumentInfoId,
            variantformat.name
        )
        return HentDokumentResponse(safHentDokumentResponseTo.dokument)
    }
}
