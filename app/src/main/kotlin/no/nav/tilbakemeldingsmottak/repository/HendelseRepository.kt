package no.nav.tilbakemeldingsmottak.repository

import no.nav.tilbakemeldingsmottak.domain.models.Hendelse
import org.springframework.data.repository.CrudRepository

interface HendelseRepository : CrudRepository<Hendelse, Long>
