package no.nav.tilbakemeldingsmottak.rest.common.pdf

import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.generer.PdfGeneratorService
import no.nav.tilbakemeldingsmottak.generer.modeller.ServiceklagePdfModell
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.Klagetyper
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class PdfService {
    fun opprettServiceklagePdf(
        request: OpprettServiceklageRequest,
        innlogget: Boolean,
        fremmet: LocalDateTime = LocalDateTime.now()
    ): ByteArray {
        return try {
            val serviceklagePdfModell = ServiceklagePdfModell(
                KANAL_SERVICEKLAGESKJEMA_ANSWER,
                if (!innlogget) "OBS! Klagen er sendt inn uinnlogget" else null,
                lagKlageMap(request, fremmet)
            )
            PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)
        } catch (e: Exception) {
            throw ServerErrorException("Opprett serviceklage PDF", e)
        }
    }

    private fun lagKlageMap(request: OpprettServiceklageRequest, fremmet: LocalDateTime): Map<String, String?> {
        val klageMap = mutableMapOf(
            "Kanal" to KANAL_SERVICEKLAGESKJEMA_ANSWER,
            "Dato fremmet" to fremmet.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        )

        request.innmelder?.let {
            if (!it.navn.isNullOrBlank()) {
                klageMap["Navn til innmelder"] = it.navn
            }
            if (!it.personnummer.isNullOrBlank()) {
                klageMap["Personnummer til innmelder"] = it.personnummer
            }
        }

        when (request.paaVegneAv) {
            PaaVegneAv.PRIVATPERSON -> {}
            PaaVegneAv.ANNEN_PERSON -> {
                request.innmelder?.let {
                    if (!it.rolle.isNullOrBlank()) {
                        klageMap["Innmelders rolle"] = it.rolle
                        klageMap["Innmelder har fullmakt"] =
                            if (it.harFullmakt == true) "Ja" else "Nei"
                    }
                }
                request.paaVegneAvPerson?.let {
                    klageMap["Navn til forulempet person"] = it.navn
                    klageMap["Personnummer til forulempet person"] = it.personnummer
                }
            }

            PaaVegneAv.BEDRIFT -> {
                request.innmelder?.let {
                    if (!it.rolle.isNullOrBlank()) {
                        klageMap["Innmelders rolle"] = it.rolle
                    }
                }
                request.paaVegneAvBedrift?.let {
                    klageMap["Navn til forulempet bedrift"] = it.navn
                    klageMap["Orgnr til forulempet bedrift"] = it.organisasjonsnummer
                }
            }

            null -> throw ServerErrorException("PaaVegneAv er ikke satt")
        }

        request.enhetsnummerPaaklaget?.let {
            klageMap["Påklaget enhet"] = it
        }

        klageMap["Klagetype"] = request.klagetyper?.joinToString(", ") { it.value }

        if (request.klagetyper?.contains(Klagetyper.LOKALT_NAV_KONTOR) == true) {
            klageMap["Gjelder økonomisk sosialhjelp/sosiale tjenester"] =
                request.gjelderSosialhjelp?.value
        }

        if (request.klagetyper?.contains(Klagetyper.ANNET) == true && !request.klagetypeUtdypning.isNullOrBlank()) {
            klageMap["Klagetype spesifisert i fritekst"] = request.klagetypeUtdypning
        }

        klageMap["Klagetekst"] = request.klagetekst

        request.oenskerAaKontaktes?.let {
            klageMap["Ønsker å kontaktes"] = if (it) "Ja" else "Nei"
        }

        request.innmelder?.telefonnummer?.let {
            klageMap["Telefonnummer til innmelder"] = it
        }

        return klageMap
    }

    fun opprettKlassifiseringPdf(questionAnswerMap: Map<String, String?>, serviceklage: Serviceklage): ByteArray {
        return try {
            var subtitle = "Journalpost: ${serviceklage.journalpostId}"

            if (serviceklage.oppgaveId != null) {
                subtitle += " - Oppgave: ${serviceklage.oppgaveId}"
            }

            val serviceklagePdfModell =
                ServiceklagePdfModell("Serviceklage klassifisering", subtitle, questionAnswerMap)
            PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)
        } catch (e: Exception) {
            throw ServerErrorException("Opprett serviceklage klassifiserings PDF", e)
        }
    }
}