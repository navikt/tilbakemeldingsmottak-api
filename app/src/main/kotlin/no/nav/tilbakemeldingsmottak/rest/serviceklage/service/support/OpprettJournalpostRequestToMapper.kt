package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv.*
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class OpprettJournalpostRequestToMapper {

    private val TEMA_SER = "SER"
    private val KANAL_NAV_NO = "NAV_NO"
    private val KANAL_NAV_NO_UINNLOGGET = "NAV_NO_UINNLOGGET"
    private val TITTEL_SERVICEKLAGE = "Serviceklage"
    private val JOURNALFOERENDE_ENHET = "9999"
    private val FILTYPE_PDFA = "PDFA"
    private val VARIANTFORMAT_ARKIV = "ARKIV"

    fun map(
        request: OpprettServiceklageRequest,
        fysiskDokument: ByteArray,
        innlogget: Boolean
    ): OpprettJournalpostRequestTo {
        val avsenderMottaker = AvsenderMottaker(
            id = request.innmelder?.personnummer,
            idType = if (request.innmelder?.personnummer != null) AvsenderMottakerIdType.FNR else null,
            navn = request.innmelder?.navn,
        )

        val sak = Sak(sakstype = Sakstype.GENERELL_SAK)

        return OpprettJournalpostRequestTo(
            avsenderMottaker = avsenderMottaker,
            sak = sak,
            bruker = mapBruker(request),
            journalpostType = JournalpostType.INNGAAENDE,
            journalfoerendeEnhet = JOURNALFOERENDE_ENHET,
            tema = TEMA_SER,
            tittel = TITTEL_SERVICEKLAGE,
            kanal = if (innlogget) KANAL_NAV_NO else KANAL_NAV_NO_UINNLOGGET,
            dokumenter = listOf(buildDokument(fysiskDokument)),
            eksternReferanseId = MDC.get(MDC_CALL_ID)
        )
    }

    private fun buildDokument(fysiskDokument: ByteArray): Dokument {
        val dokumentVariant = DokumentVariant(
            filtype = FILTYPE_PDFA,
            fysiskDokument = fysiskDokument,
            variantformat = VARIANTFORMAT_ARKIV
        )

        return Dokument(tittel = TITTEL_SERVICEKLAGE, dokumentvarianter = listOf(dokumentVariant))
    }

    private fun mapBruker(request: OpprettServiceklageRequest): Bruker {
        return when (request.paaVegneAv) {
            PRIVATPERSON -> Bruker(
                id = request.innmelder?.personnummer!!,
                idType = BrukerIdType.FNR
            )

            ANNEN_PERSON -> Bruker(
                id = request.paaVegneAvPerson?.personnummer!!,
                idType = BrukerIdType.FNR
            )

            BEDRIFT -> Bruker(
                id = request.paaVegneAvBedrift?.organisasjonsnummer!!,
                idType = BrukerIdType.ORGNR
            )

            null -> throw IllegalArgumentException("På vegne av må være satt")
        }
    }
}
