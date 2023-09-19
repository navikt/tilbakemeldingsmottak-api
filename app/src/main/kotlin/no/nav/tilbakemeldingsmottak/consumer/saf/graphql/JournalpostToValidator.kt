package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.util.ValidationUtil
import java.util.function.Consumer

class JournalpostToValidator {
    fun validateAndReturn(safJournalpostTo: SafJournalpostTo): SafJournalpostTo {
        ValidationUtil.assertJournalpostFieldNotNull(
            SafJournalpostTo.DokumentInfo::class.java,
            safJournalpostTo.dokumenter
        )
        validateDokumenter(safJournalpostTo.dokumenter)
        return safJournalpostTo
    }

    private fun validateDokumenter(dokumenter: List<SafJournalpostTo.DokumentInfo>) {
        dokumenter.forEach(Consumer { dokumentInfo: SafJournalpostTo.DokumentInfo -> validateDokument(dokumentInfo) })
    }

    private fun validateDokument(dokumentInfo: SafJournalpostTo.DokumentInfo) {
        ValidationUtil.assertDokumentFieldNotNullOrEmpty("dokumentInfoId", dokumentInfo.dokumentInfoId)
        validateDokumentVarianter(dokumentInfo.dokumentvarianter)
    }

    private fun validateDokumentVarianter(dokumentvarianter: List<SafJournalpostTo.Dokumentvariant>) {
        dokumentvarianter.forEach(Consumer { dokumentvariant: SafJournalpostTo.Dokumentvariant ->
            validateAndReturnDokumentVariant(
                dokumentvariant
            )
        })
    }

    private fun validateAndReturnDokumentVariant(dokumentvariant: SafJournalpostTo.Dokumentvariant) {
        ValidationUtil.assertNotNullOrEmpty("variantformat", dokumentvariant.variantformat)
    }
}
