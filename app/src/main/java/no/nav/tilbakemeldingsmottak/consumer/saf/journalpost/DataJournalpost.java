package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost;

import java.io.Serializable;

public class DataJournalpost implements Serializable {
	private SafJournalpostTo journalpost;

	public SafJournalpostTo getJournalpost() {
		return journalpost;
	}

	public void setJournalpost(SafJournalpostTo journalpost) {
		this.journalpost = journalpost;
	}
}