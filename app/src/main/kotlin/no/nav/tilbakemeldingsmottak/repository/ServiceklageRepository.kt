package no.nav.tilbakemeldingsmottak.repository

import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ServiceklageRepository : CrudRepository<Serviceklage, Long> {
    fun findByJournalpostId(journalpostId: String): Serviceklage?
    fun findAllByOpprettetDatoAfterOrAvsluttetDatoAfter(
        opprettetDato: LocalDateTime,
        avsluttetDato: LocalDateTime
    ): List<Serviceklage>

    @Modifying
    @Query("DELETE FROM Serviceklage WHERE avsluttetDato IS NOT NULL AND avsluttetDato < :cutoffDate")
    fun deleteServiceklageOlderThan(cutoffDate: LocalDateTime)
}
