package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.domain.Serviceklage
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OpprettServiceklageRequestMapper {
    fun map(request: OpprettServiceklageRequest, innlogget: Boolean): Serviceklage {
        val currentDateTime = LocalDateTime.now()
        return Serviceklage(
            opprettetDato = currentDateTime,
            fremmetDato = currentDateTime.toLocalDate(),
            innsender = request.paaVegneAv?.value,
            klagenGjelderId = findKlagenGjelderId(request),
            innlogget = innlogget,
            klagetyper = mapKlagetype(request.klagetyper),
            klagetypeUtdypning = mapKlagetypeUtdypning(request),
            gjelderSosialhjelp = request.gjelderSosialhjelp?.value,
            klagetekst = request.klagetekst,
            svarmetode = mapSvarmetode(request.oenskerAaKontaktes),
            svarmetodeUtdypning = mapSvarmetodeUtdypning(request.oenskerAaKontaktes),
            kanal = KANAL_SERVICEKLAGESKJEMA_ANSWER,
            enhetsnummerPaaklaget = mapEnhetsnummerPaaklaget(request),
        )
    }

    fun mapKlagetypeUtdypning(request: OpprettServiceklageRequest): String? {
        return if (request.klagetyper?.contains(OpprettServiceklageRequest.Klagetyper.ANNET) == true) {
            request.klagetypeUtdypning
        } else null
    }

    private fun mapKlagetype(klagetype: List<OpprettServiceklageRequest.Klagetyper>?): String {
        return klagetype?.joinToString(", ") { it.value } ?: ""
    }

    private fun mapSvarmetode(oenskerAaKontaktes: Boolean?): String {
        return oenskerAaKontaktes?.let { if (!it) SVAR_IKKE_NOEDVENDIG_ANSWER else null } ?: SVAR_IKKE_NOEDVENDIG_ANSWER
    }

    private fun mapSvarmetodeUtdypning(oenskerAaKontaktes: Boolean?): String {
        return oenskerAaKontaktes?.let { if (!it) BRUKER_IKKE_BEDT_OM_SVAR_ANSWER else null }
            ?: INNMELDER_MANGLER_FULLMAKT_ANSWER
    }

    fun mapEnhetsnummerPaaklaget(request: OpprettServiceklageRequest): String? {
        return if (request.enhetsnummerPaaklaget?.isNotBlank() == true) {
            request.enhetsnummerPaaklaget
        } else null
    }

    private fun findKlagenGjelderId(request: OpprettServiceklageRequest): String? {
        return when (request.paaVegneAv) {
            PRIVATPERSON -> request.innmelder?.personnummer
            ANNEN_PERSON -> request.paaVegneAvPerson?.personnummer
            BEDRIFT -> request.paaVegneAvBedrift?.organisasjonsnummer
            null -> throw IllegalArgumentException("PaaVegneAv kan ikke være null")
        }
    }
}
