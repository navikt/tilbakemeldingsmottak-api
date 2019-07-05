package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.OpprettJournalpostRequestTo;
import org.springframework.stereotype.Component;

@Component
public class OpprettJournalpostRequestToMapper {

    public OpprettJournalpostRequestTo map(OpprettServiceklageRequest request) {
        return OpprettJournalpostRequestTo.builder()

                .build();
    }

}