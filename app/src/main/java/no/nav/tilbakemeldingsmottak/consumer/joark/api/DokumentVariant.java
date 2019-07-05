package no.nav.tilbakemeldingsmottak.consumer.joark.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DokumentVariant {
	private String filtype;
	private String variantformat;
	private byte[] fysiskDokument;
}
