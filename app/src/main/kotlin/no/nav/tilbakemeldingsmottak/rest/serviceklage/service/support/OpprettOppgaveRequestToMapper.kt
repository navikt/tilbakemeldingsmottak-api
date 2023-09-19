package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo
import no.nav.tilbakemeldingsmottak.consumer.pdl.PdlService
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class OpprettOppgaveRequestToMapper(private val pdlService: PdlService) {

    private val KLAGEINSTANS_ENHETSNR = "4200"
    private val FAGPOST_ENHETSNR = "2950"
    private val PRIORITET = "NORM"
    private val SERVICEKLAGE_TEMA = "SER"
    private val RETTING_TEMA = "RPO"
    private val OPPGAVETYPE_VUR = "VUR"
    private val OPPGAVETYPE_JFR = "JFR"
    private val JOURNALSTATUS_ENDELIG = "ENDELIG"
    private val BESKRIVELSE_SLETTING = "Skal slettes da det ikke er en serviceklage"
    private val DAGER_FRIST = 18L

    fun mapServiceklageOppgave(
        klagenGjelderId: String,
        paaVegneAv: PaaVegneAv,
        opprettJournalpostResponseTo: OpprettJournalpostResponseTo
    ): OpprettOppgaveRequestTo {
        return OpprettOppgaveRequestTo(
            tildeltEnhetsnr = KLAGEINSTANS_ENHETSNR,
            prioritet = PRIORITET,
            aktoerId = if (paaVegneAv == PaaVegneAv.BEDRIFT) "" else pdlService.hentAktorIdForIdent(klagenGjelderId),
            orgnr = if (paaVegneAv == PaaVegneAv.BEDRIFT) klagenGjelderId else "",
            aktivDato = LocalDate.now().toString(),
            journalpostId = opprettJournalpostResponseTo.journalpostId,
            tema = SERVICEKLAGE_TEMA,
            oppgavetype = if (JOURNALSTATUS_ENDELIG == opprettJournalpostResponseTo.journalstatus) OPPGAVETYPE_VUR else OPPGAVETYPE_JFR,
            fristFerdigstillelse = LocalDate.now().plusDays(DAGER_FRIST).toString()
        )
    }

    fun mapSlettingOppgave(hentOppgaveResponseTo: HentOppgaveResponseTo): OpprettOppgaveRequestTo {
        return OpprettOppgaveRequestTo(
            tildeltEnhetsnr = FAGPOST_ENHETSNR,
            prioritet = PRIORITET,
            aktoerId = hentOppgaveResponseTo.aktoerId,
            orgnr = hentOppgaveResponseTo.orgnr,
            beskrivelse = BESKRIVELSE_SLETTING,
            aktivDato = LocalDate.now().toString(),
            journalpostId = hentOppgaveResponseTo.journalpostId,
            tema = RETTING_TEMA,
            oppgavetype = OPPGAVETYPE_VUR,
            fristFerdigstillelse = LocalDate.now().plusDays(DAGER_FRIST).toString()
        )
    }
}
