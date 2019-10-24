package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class SafJournalpostTo {

	@Builder.Default
	private final List<DokumentInfo> dokumenter = new ArrayList<>();

	@Value
	@Builder
	public static class DokumentInfo {
		private final String dokumentInfoId;

		@Builder.Default
		private final List<Dokumentvariant> dokumentvarianter = new ArrayList<>();
	}

	@Value
	@Builder
	public static class Dokumentvariant {
		private final String variantformat;
		private final boolean saksbehandlerHarTilgang;
	}
}
