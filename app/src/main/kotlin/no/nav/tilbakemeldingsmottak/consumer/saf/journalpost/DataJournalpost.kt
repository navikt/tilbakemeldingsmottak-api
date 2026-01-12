package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost

import java.io.Serializable
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost


class DataJournalpost : Serializable {
    var journalpost: Journalpost? = null
}