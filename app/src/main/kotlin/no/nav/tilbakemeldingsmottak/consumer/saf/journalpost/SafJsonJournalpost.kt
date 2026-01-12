package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost

import java.io.Serializable
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost

class SafJsonJournalpost : Serializable {
    var data: DataJournalpost? = null
    val journalpost: Journalpost?
        get() = data?.journalpost
}
