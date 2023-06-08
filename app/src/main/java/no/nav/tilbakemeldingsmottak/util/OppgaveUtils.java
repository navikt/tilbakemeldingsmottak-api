package no.nav.tilbakemeldingsmottak.util;

import lombok.NoArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.NoContentException;

@NoArgsConstructor
public class OppgaveUtils {

    private static final String FERDIGSTILT = "FERDIGSTILT";

    public static void assertIkkeFerdigstilt(HentOppgaveResponseTo hentOppgaveResponseTo) {
        if (FERDIGSTILT.equals(hentOppgaveResponseTo.getStatus())) {
            throw new ClientErrorException(String.format("Oppgave med oppgaveId=%s er allerede ferdigstilt",
                    hentOppgaveResponseTo.getId()), ErrorCode.OPPGAVE_COMPLETED);
        }
    }

    public static void assertHarJournalpost(HentOppgaveResponseTo hentOppgaveResponseTo) {
        if (hentOppgaveResponseTo.getJournalpostId() == null || "".equals(hentOppgaveResponseTo.getJournalpostId())) {
            throw new NoContentException(String.format("Oppgave med oppgaveId=%s har ikke tilknyttet dokument i arkivet",
                    hentOppgaveResponseTo.getId()), ErrorCode.OPPGAVE_MISSING_JOURNALPOST);
        }
    }

}
