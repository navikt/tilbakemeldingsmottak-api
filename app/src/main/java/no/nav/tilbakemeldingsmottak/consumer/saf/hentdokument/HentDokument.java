package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument;

/**
 * @author Sigurd Midttun, Visma Consulting.
 */
public interface HentDokument {

	HentDokumentResponseTo hentDokument(String journalpostId, String dokumentInfoId, String variantFormat);

}
