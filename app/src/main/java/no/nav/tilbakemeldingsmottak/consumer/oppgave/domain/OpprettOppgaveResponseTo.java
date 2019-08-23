package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpprettOppgaveResponseTo {
    private String id;
    private int versjon;
}
