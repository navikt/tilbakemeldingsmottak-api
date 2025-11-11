package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.util.ValidationUtil
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.DokumentInfo
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Dokumentvariant
import java.util.function.Consumer

class JournalpostToValidator {
    fun validateAndReturn(safJournalpostTo: Journalpost): Journalpost {

        ValidationUtil.assertJournalpostFieldNotNull(
            DokumentInfo::class.java,
            safJournalpostTo.dokumenter
        )
        validateDokumenter(safJournalpostTo.dokumenter!!)
        return safJournalpostTo
    }

    private fun validateDokumenter(dokumenter: List<DokumentInfo?>) {
        dokumenter.forEach(Consumer { dokumentInfo -> validateDokument(dokumentInfo) })
    }

    private fun validateDokument(dokumentInfo: DokumentInfo?) {
        ValidationUtil.assertJournalpostFieldNotNull(
            DokumentInfo::class.java,
            dokumentInfo
        )
        ValidationUtil.assertDokumentFieldNotNullOrEmpty("dokumentInfoId", dokumentInfo?.dokumentInfoId)
        validateDokumentVarianter(dokumentInfo?.dokumentvarianter)
    }

    private fun validateDokumentVarianter(dokumentvarianter: List<Dokumentvariant?>?) {
        ValidationUtil.assertJournalpostFieldNotNull(
            Dokumentvariant::class.java,
            dokumentvarianter
        )
        dokumentvarianter?.forEach(Consumer { dokumentvariant ->
            validateAndReturnDokumentVariant(
                dokumentvariant
            )
        })
    }

    private fun validateAndReturnDokumentVariant(dokumentvariant: Dokumentvariant?) {
        ValidationUtil.assertNotNullOrEmpty("variantformat", dokumentvariant?.variantformat?.name)
    }
}
