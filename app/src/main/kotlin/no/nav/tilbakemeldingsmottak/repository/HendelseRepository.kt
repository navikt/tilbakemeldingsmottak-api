package no.nav.tilbakemeldingsmottak.repository

import no.nav.tilbakemeldingsmottak.domain.models.Hendelse
import org.springframework.data.repository.CrudRepository

interface HendelseRepository : CrudRepository<Hendelse, Long> {
    fun findAllByJournalpostId(journalpostId: String): List<Hendelse>
    fun findAllByOppgaveId(oppgaveId: String): List<Hendelse>
}
