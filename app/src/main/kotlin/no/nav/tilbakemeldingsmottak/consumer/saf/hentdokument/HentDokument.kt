package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument

interface HentDokument {
    fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: String,
    ): HentDokumentResponseTo
}
