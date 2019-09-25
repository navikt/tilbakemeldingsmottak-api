package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EndreOppgaveRequestTo {
    String id;
    String tildeltEnhetsnr;
    String tema;
    String versjon;
    String aktivDato;
    String prioritet;
    String oppgavetype;
    String journalpostId;
    String status;
}
