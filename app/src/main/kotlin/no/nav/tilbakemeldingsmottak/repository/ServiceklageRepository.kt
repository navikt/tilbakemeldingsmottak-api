package no.nav.tilbakemeldingsmottak.repository

import no.nav.tilbakemeldingsmottak.domain.Serviceklage
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ServiceklageRepository : CrudRepository<Serviceklage, Long> {
    fun findByJournalpostId(journalpostId: String): Serviceklage?
    fun findAllByOpprettetDatoAfterOrAvsluttetDatoAfter(
        opprettetDato: LocalDateTime,
        avsluttetDato: LocalDateTime
    ): List<Serviceklage>
}
