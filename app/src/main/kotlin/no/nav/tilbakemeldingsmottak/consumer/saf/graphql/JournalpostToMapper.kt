package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat
import java.time.LocalDateTime

class JournalpostToMapper {

    fun map(safJournalpostTo: SafJournalpostTo): Journalpost {
        return Journalpost(
            dokumenter = mapDokumenter(safJournalpostTo.dokumenter),
            bruker = mapBruker(safJournalpostTo.bruker),
            datoOpprettet = mapDatoOpprettet(safJournalpostTo.datoOpprettet)
        )
    }

    private fun mapDokumenter(dokumenter: List<SafJournalpostTo.DokumentInfo>): List<Journalpost.DokumentInfo> {
        return dokumenter.map { mapDokument(it) }
    }

    private fun mapDokument(dokumentInfo: SafJournalpostTo.DokumentInfo): Journalpost.DokumentInfo {
        return Journalpost.DokumentInfo(
            dokumentInfoId = dokumentInfo.dokumentInfoId,
            dokumentvarianter = mapDokumentVarianter(dokumentInfo.dokumentvarianter)
        )
    }

    private fun mapDokumentVarianter(dokumentvarianter: List<SafJournalpostTo.Dokumentvariant>): List<Journalpost.Dokumentvariant> {
        return dokumentvarianter.filter { isVariantformatArkivOrSladdet(it) }.map { mapDokumentVariant(it) }
    }

    private fun mapDokumentVariant(dokumentvariant: SafJournalpostTo.Dokumentvariant): Journalpost.Dokumentvariant {
        return Journalpost.Dokumentvariant(
            variantformat = Variantformat.valueOf(dokumentvariant.variantformat),
            saksbehandlerHarTilgang = dokumentvariant.saksbehandlerHarTilgang
        )
    }

    private fun mapBruker(bruker: SafJournalpostTo.Bruker): Journalpost.Bruker {
        return Journalpost.Bruker(id = bruker.id)
    }

    private fun mapDatoOpprettet(datoOpprettet: String): LocalDateTime {
        return LocalDateTime.parse(datoOpprettet)
    }

    private fun isVariantformatArkivOrSladdet(dokumentvariant: SafJournalpostTo.Dokumentvariant): Boolean {
        return dokumentvariant.variantformat == Variantformat.ARKIV.name || dokumentvariant.variantformat == Variantformat.SLADDET.name
    }
}
