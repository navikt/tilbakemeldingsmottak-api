package no.nav.tilbakemeldingsmottak.serviceklage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpprettServiceklageResponse {
    String message;
    String serviceklageId;
    String journalpostId;
    String oppgaveId;
}
