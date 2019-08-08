package no.nav.tilbakemeldingsmottak.consumer.joark.domain;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class Dokument {

	private String tittel;
	private String brevkode;
	private String dokumentKategori;
	private List<DokumentVariant> dokumentvarianter = new ArrayList<>();
}
