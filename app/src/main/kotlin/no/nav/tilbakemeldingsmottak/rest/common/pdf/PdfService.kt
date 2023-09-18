package no.nav.tilbakemeldingsmottak.rest.common.pdf

import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.generer.PdfGeneratorService
import no.nav.tilbakemeldingsmottak.generer.modeller.ServiceklagePdfModell
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.Klagetyper
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

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
        val klageMap: MutableMap<String, String?> = HashMap()
        klageMap["Kanal"] = KANAL_SERVICEKLAGESKJEMA_ANSWER
        klageMap["Dato fremmet"] = fremmet.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        if (!StringUtils.isBlank(request.innmelder!!.navn)) {
            klageMap["Navn til innmelder"] = request.innmelder!!.navn
        }
        if (!StringUtils.isBlank(request.innmelder!!.personnummer)) {
            klageMap["Personnummer til innmelder"] = request.innmelder!!.personnummer
        }

        when (request.paaVegneAv) {
            PaaVegneAv.PRIVATPERSON -> {}
            PaaVegneAv.ANNEN_PERSON -> {
                if (!StringUtils.isBlank(request.innmelder!!.rolle)) {
                    klageMap["Innmelders rolle"] = request.innmelder!!.rolle
                    klageMap["Innmelder har fullmakt"] = if (request.innmelder!!.harFullmakt!!) "Ja" else "Nei"
                }
                klageMap["Navn til forulempet person"] = request.paaVegneAvPerson!!.navn
                klageMap["Personnummer til forulempet person"] = request.paaVegneAvPerson!!.personnummer
            }

            PaaVegneAv.BEDRIFT -> {
                if (!StringUtils.isBlank(request.innmelder!!.rolle)) {
                    klageMap["Innmelders rolle"] = request.innmelder!!.rolle
                    klageMap["Innmelder har fullmakt"] = if (request.innmelder!!.harFullmakt!!) "Ja" else "Nei"
                }
                klageMap["Navn til forulempet bedrift"] = request.paaVegneAvBedrift!!.navn
                klageMap["Orgnr til forulempet bedrift"] = request.paaVegneAvBedrift!!.organisasjonsnummer
            }

            null -> throw ServerErrorException("PaaVegneAv er ikke satt")
        }
        if (!StringUtils.isBlank(request.enhetsnummerPaaklaget)) {
            klageMap["Påklaget enhet"] = request.enhetsnummerPaaklaget
        }
        klageMap["Klagetype"] = StringUtils.join(
            request.klagetyper!!.stream().map { x: Klagetyper -> x.value }.collect(Collectors.toList()), ", "
        )
        if (request.klagetyper!!.contains(Klagetyper.LOKALT_NAV_KONTOR)) {
            klageMap["Gjelder økonomisk sosialhjelp/sosiale tjenester"] = request.gjelderSosialhjelp!!.value
        }
        if (request.klagetyper!!.contains(Klagetyper.ANNET) && !StringUtils.isBlank(request.klagetypeUtdypning)) {
            klageMap["Klagetype spesifisert i fritekst"] = request.klagetypeUtdypning
        }
        klageMap["Klagetekst"] = request.klagetekst
        if (request.oenskerAaKontaktes != null) {
            klageMap["Ønsker å kontaktes"] = if (request.oenskerAaKontaktes!!) "Ja" else "Nei"
        }
        if (!StringUtils.isBlank(request.innmelder!!.telefonnummer)) {
            klageMap["Telefonnummer til innmelder"] = request.innmelder!!.telefonnummer
        }
        return klageMap
    }

    fun opprettKlassifiseringPdf(questionAnswerMap: Map<String, String?>): ByteArray {
        return try {
            val serviceklagePdfModell = ServiceklagePdfModell("Serviceklage klassifisering", null, questionAnswerMap)
            PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)
        } catch (e: Exception) {
            throw ServerErrorException("Opprett serviceklage klassifiserings PDF", e)
        }
    }
}