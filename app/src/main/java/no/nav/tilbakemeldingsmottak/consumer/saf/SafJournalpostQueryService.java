package no.nav.tilbakemeldingsmottak.consumer.saf;

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost;

public interface SafJournalpostQueryService {

    Journalpost hentJournalpost(String journalpostid, String authorizationHeader);

}
