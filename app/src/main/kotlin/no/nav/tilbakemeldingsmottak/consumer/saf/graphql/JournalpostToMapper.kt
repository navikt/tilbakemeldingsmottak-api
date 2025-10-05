package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.JournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.VariantformatTo
import java.time.LocalDateTime

class JournalpostToMapper {

    fun map(safJournalpostTo: SafJournalpostTo): JournalpostTo {
        return JournalpostTo(
            dokumenter = mapDokumenter(safJournalpostTo.dokumenter),
            bruker = mapBruker(safJournalpostTo.bruker),
            datoOpprettet = mapDatoOpprettet(safJournalpostTo.datoOpprettet)
        )
    }

    private fun mapDokumenter(dokumenter: List<SafJournalpostTo.DokumentInfo>): List<JournalpostTo.DokumentInfo> {
        return dokumenter.map { mapDokument(it) }
    }

    private fun mapDokument(dokumentInfo: SafJournalpostTo.DokumentInfo): JournalpostTo.DokumentInfo {
        return JournalpostTo.DokumentInfo(
            dokumentInfoId = dokumentInfo.dokumentInfoId,
            dokumentvarianter = mapDokumentVarianter(dokumentInfo.dokumentvarianter)
        )
    }

    private fun mapDokumentVarianter(dokumentvarianter: List<SafJournalpostTo.Dokumentvariant>): List<JournalpostTo.Dokumentvariant> {
        return dokumentvarianter.filter { isVariantformatArkivOrSladdet(it) }.map { mapDokumentVariant(it) }
    }

    private fun mapDokumentVariant(dokumentvariant: SafJournalpostTo.Dokumentvariant): JournalpostTo.Dokumentvariant {
        return JournalpostTo.Dokumentvariant(
            variantformat = VariantformatTo.valueOf(dokumentvariant.variantformat),
            saksbehandlerHarTilgang = dokumentvariant.saksbehandlerHarTilgang
        )
    }

    private fun mapBruker(bruker: SafJournalpostTo.Bruker): JournalpostTo.Bruker {
        return JournalpostTo.Bruker(id = bruker.id)
    }

    private fun mapDatoOpprettet(datoOpprettet: String): LocalDateTime {
        return LocalDateTime.parse(datoOpprettet)
    }

    private fun isVariantformatArkivOrSladdet(dokumentvariant: SafJournalpostTo.Dokumentvariant): Boolean {
        return dokumentvariant.variantformat == VariantformatTo.ARKIV.name || dokumentvariant.variantformat == VariantformatTo.SLADDET.name
    }
}
