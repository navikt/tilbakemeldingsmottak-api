package no.nav.tilbakemeldingsmottak.repository;

import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
public interface ServiceklageRepository extends CrudRepository<Serviceklage, Long> {
    Serviceklage findByJournalpostId(String journalpostId);
}
