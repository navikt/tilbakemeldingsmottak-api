package no.nav.tilbakemeldingsmottak.consumer.joark.domain;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class OpprettJournalpostRequestTo {

	private JournalpostType journalpostType;
	private AvsenderMottaker avsenderMottaker;
	private Bruker bruker;
	private String tema;
	private String behandlingstema;
	private String tittel;
	private String kanal;
	private String journalfoerendeEnhet;
	private String eksternReferanseId;
	private List<Tilleggsopplysning> tilleggsopplysninger = new ArrayList<>();
	private Sak sak;
	private List<Dokument> dokumenter = new ArrayList<>();

}
