package no.nav.tilbakemeldingsmottak.repository;

import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
public interface ServiceklageRepository extends CrudRepository<Serviceklage, Long> {
    Serviceklage findByJournalpostId(String journalpostId);

    List<Serviceklage> findAllByOpprettetDatoAfterOrAvsluttetDatoAfter(LocalDateTime opprettetDato, LocalDateTime avsluttetDato);
}
