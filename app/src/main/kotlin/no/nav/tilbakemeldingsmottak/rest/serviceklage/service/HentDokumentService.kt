package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import no.nav.tilbakemeldingsmottak.consumer.saf.SafJournalpostQueryService
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentConsumer
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.NoContentException
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.springframework.stereotype.Service

@Service
class HentDokumentService(
    private val safJournalpostQueryService: SafJournalpostQueryService,
    private val hentDokumentConsumer: HentDokumentConsumer,
    private val oidcUtils: OidcUtils
) {
    fun hentDokument(journalpostId: String): HentDokumentResponse {
        val authorizationHeader = "Bearer ${oidcUtils.getFirstValidToken()}"
        val journalpost = safJournalpostQueryService.hentJournalpost(journalpostId, authorizationHeader)

        val variantformat: Variantformat
        val dokumentInfo: Journalpost.DokumentInfo
        if (journalpost.dokumenter.isEmpty()) {
            throw NoContentException(
                String.format("Fant ingen dokument på journalpost %s", journalpostId),
                ErrorCode.JOURNALPOST_MISSING_DOKUMENT
            )
        } else {
            dokumentInfo = journalpost.dokumenter[0]
            variantformat = when {
                dokumentInfo.dokumentvarianter.any { it.variantformat == Variantformat.SLADDET } -> Variantformat.SLADDET
                dokumentInfo.dokumentvarianter.any { it.variantformat == Variantformat.ARKIV } -> Variantformat.ARKIV
                else -> throw NoContentException(
                    "Fant ingen tilgjengelig dokument på journalpost $journalpostId",
                    ErrorCode.JOURNALPOST_MISSING_DOKUMENT
                )
            }
        }
        val safHentDokumentResponseTo = hentDokumentConsumer.hentDokument(
            journalpostId,
            dokumentInfo.dokumentInfoId,
            variantformat.name,
            authorizationHeader
        )
        return HentDokumentResponse(safHentDokumentResponseTo.dokument)
    }
}
