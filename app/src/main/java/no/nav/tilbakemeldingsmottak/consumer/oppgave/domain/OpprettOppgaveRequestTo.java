package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpprettOppgaveRequestTo {
    String tildeltEnhetsnr;
    String opprettetAvEnhetsnr;
    String aktoerId;
    String journalpostId;
    String journalpostkilde;
    String behandlesAvApplikasjon;
    String saksreferanse;
    String orgnr;
    String bnr;
    String samhandlernr;
    String tilordnetRessurs;
    String beskrivelse;
    String temagruppe;
    String tema;
    String behandlingstema;
    String oppgavetype;
    String behandlingstype;
    String mappeId;
    String aktivDato;
    String fristFerdigstillelse;
    String prioritet;
}
