package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import no.nav.tilbakemeldingsmottak.domain.enums.HendelseType
import no.nav.tilbakemeldingsmottak.domain.models.Hendelse
import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import no.nav.tilbakemeldingsmottak.repository.HendelseRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HendelseService(private val hendelseRepository: HendelseRepository) {
    fun saveHendelse(hendelseType: HendelseType, journalpostId: String?, oppgaveId: String?) {
        val hendelse = Hendelse(
            journalpostId = journalpostId,
            oppgaveId = oppgaveId,
            hendelsetype = hendelseType.name,
            tidspunkt = LocalDateTime.now()
        )
        hendelseRepository.save(hendelse)
    }

    fun saveHendelse(hendelse: Hendelse) {
        hendelseRepository.save(hendelse)
    }

    fun createServiceklage(serviceklage: Serviceklage) {
        saveHendelse(
            hendelseType = HendelseType.OPPRETT_SERVICEKLAGE,
            journalpostId = serviceklage.journalpostId,
            oppgaveId = null // FIXME: Add oppgaveId
        )
    }

    fun classifyServiceklage(serviceklage: Serviceklage) {
        saveHendelse(
            hendelseType = HendelseType.KLASSIFISER_SERVICEKLAGE,
            journalpostId = serviceklage.journalpostId,
            oppgaveId = null // FIXME: Add oppgaveId
        )
    }
}