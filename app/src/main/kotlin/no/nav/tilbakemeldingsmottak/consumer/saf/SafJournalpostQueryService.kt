package no.nav.tilbakemeldingsmottak.consumer.saf

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost

interface SafJournalpostQueryService {
    fun hentJournalpost(journalpostid: String, authorizationHeader: String): Journalpost
}
