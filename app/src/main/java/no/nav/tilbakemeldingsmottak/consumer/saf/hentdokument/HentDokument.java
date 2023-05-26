package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument;

public interface HentDokument {

    HentDokumentResponseTo hentDokument(String journalpostId, String dokumentInfoId, String variantFormat, String token);

}
