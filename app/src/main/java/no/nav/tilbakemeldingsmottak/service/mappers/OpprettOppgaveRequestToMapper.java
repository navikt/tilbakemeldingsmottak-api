package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.api.OpprettOppgaveRequestTo;
import org.springframework.stereotype.Component;

@Component
public class OpprettOppgaveRequestToMapper {
    public OpprettOppgaveRequestTo map(OpprettServiceklageRequest request) {
        return OpprettOppgaveRequestTo.builder()

                .build();
    }
}
