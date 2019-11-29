package no.nav.tilbakemeldingsmottak.util;

import lombok.NoArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.OppgaveAlleredeFerdigstiltException;

@NoArgsConstructor
public class OppgaveUtils {

    private static final String FERDIGSTILT = "FERDIGSTILT";

    public static void assertIkkeFerdigstilt(HentOppgaveResponseTo hentOppgaveResponseTo) {
        if (FERDIGSTILT.equals(hentOppgaveResponseTo.getStatus())) {
            throw new OppgaveAlleredeFerdigstiltException(String.format("Oppgave med oppgaveId=%s er allerede ferdigstilt",
                    hentOppgaveResponseTo.getId()));
        }
    }

}
