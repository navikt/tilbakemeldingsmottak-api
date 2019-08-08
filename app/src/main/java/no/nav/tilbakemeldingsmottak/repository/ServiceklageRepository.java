package no.nav.tilbakemeldingsmottak.repository;

import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
public interface ServiceklageRepository extends CrudRepository<Serviceklage, Long> {
    List<Serviceklage> findAllByKlagenGjelderId(String klagenGjelderId);
}
