package no.nav.tilbakemeldingsmottak.consumer.joark.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpprettJournalpostResponseTo {
	private String journalpostId;
	private String journalstatus;
	private String melding;
}
