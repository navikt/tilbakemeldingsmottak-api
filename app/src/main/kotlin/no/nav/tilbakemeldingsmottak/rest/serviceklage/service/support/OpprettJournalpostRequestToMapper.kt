package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.consumer.joark.domain.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv.*
import org.springframework.stereotype.Component

@Component
class OpprettJournalpostRequestToMapper {

    companion object {
        private const val TEMA_SER = "SER"
        private const val KANAL_NAV_NO = "NAV_NO"
        private const val KANAL_NAV_NO_UINNLOGGET = "NAV_NO_UINNLOGGET"
        private const val TITTEL_SERVICEKLAGE = "Serviceklage"
        private const val JOURNALFOERENDE_ENHET = "9999"
        private const val FILTYPE_PDFA = "PDFA"
        private const val VARIANTFORMAT_ARKIV = "ARKIV"
    }

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
            dokumenter = listOf(buildDokument(fysiskDokument))
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
