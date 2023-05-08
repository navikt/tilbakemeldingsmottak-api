package no.nav.tilbakemeldingsmottak.consumer.saf.graphql;

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;

import java.util.List;

import static no.nav.tilbakemeldingsmottak.consumer.saf.util.ValidationUtil.*;

public class JournalpostToValidator {

    public SafJournalpostTo validateAndReturn(SafJournalpostTo safJournalpostTo) {
        assertJournalpostFieldNotNull(SafJournalpostTo.DokumentInfo.class, safJournalpostTo.getDokumenter());
        validateDokumenter(safJournalpostTo.getDokumenter());

        return safJournalpostTo;
    }

    private void validateDokumenter(List<SafJournalpostTo.DokumentInfo> dokumenter) {
        dokumenter.forEach(this::validateDokument);
    }

    private void validateDokument(SafJournalpostTo.DokumentInfo dokumentInfo) {
        assertDokumentFieldNotNullOrEmpty("dokumentInfoId", dokumentInfo.getDokumentInfoId());
        validateDokumentVarianter(dokumentInfo.getDokumentvarianter());
    }

    private void validateDokumentVarianter(List<SafJournalpostTo.Dokumentvariant> dokumentvarianter) {
        dokumentvarianter.forEach(this::validateAndReturnDokumentVariant);
    }

    private void validateAndReturnDokumentVariant(SafJournalpostTo.Dokumentvariant dokumentvariant) {
        assertNotNullOrEmpty("variantformat", dokumentvariant.getVariantformat());
    }
}
