package no.nav.serviceklagemottak.repository;

import no.nav.serviceklagemottak.domain.Serviceklage;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
public interface ServiceklageRepository extends CrudRepository<Serviceklage, Long> {

}
