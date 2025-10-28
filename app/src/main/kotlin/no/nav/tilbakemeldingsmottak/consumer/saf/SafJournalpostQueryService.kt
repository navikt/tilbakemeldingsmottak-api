package no.nav.tilbakemeldingsmottak.consumer.saf

import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost

interface SafJournalpostQueryService {
    fun hentJournalpost(journalpostid: String): Journalpost
}
