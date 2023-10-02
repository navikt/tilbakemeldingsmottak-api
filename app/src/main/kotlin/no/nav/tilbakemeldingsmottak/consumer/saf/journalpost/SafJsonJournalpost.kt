package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost

import java.io.Serializable

class SafJsonJournalpost : Serializable {
    var data: DataJournalpost? = null
    val journalpost: SafJournalpostTo?
        get() = data?.journalpost
}
