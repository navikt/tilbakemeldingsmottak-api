package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation

import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer
import no.nav.tilbakemeldingsmottak.consumer.pdl.PdlService
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.Klagetyper
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import no.nav.tilbakemeldingsmottak.rest.common.validation.PersonnummerValidator
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class OpprettServiceklageValidator(
    private val eregConsumer: EregConsumer,
    private val personnummerValidator: PersonnummerValidator,
    private val pdlService: PdlService
) : RequestValidator() {

    private val ENHETSNUMMER_LENGTH = 4

    fun validateRequest(request: OpprettServiceklageRequest, paloggetBruker: String?) {
        validateCommonRequiredFields(request)
        when (request.paaVegneAv) {
            PaaVegneAv.PRIVATPERSON -> validatePaaVegneAvPrivatperson(request, paloggetBruker)
            PaaVegneAv.ANNEN_PERSON -> validatePaaVegneAvAnnenPerson(request)
            PaaVegneAv.BEDRIFT -> validatePaaVegneAvBedrift(request)
            null -> throw ClientErrorException("paaVegneAv kan ikke være null")
        }
    }

    private fun validateCommonRequiredFields(request: OpprettServiceklageRequest) {
        isNotNull(request.klagetyper, "klagetyper")
        if (request.klagetyper?.contains(Klagetyper.LOKALT_NAV_KONTOR) == true) {
            isNotNull(request.gjelderSosialhjelp, "gjelderSosialhjelp", " dersom klagetyper=LOKALT_NAV_KONTOR")
        }
        isNotNull(request.paaVegneAv, "paaVegneAv")
        isNotNull(request.innmelder, "innmelder")
        hasText(request.klagetekst, "klagetekst")
    }

    private fun validatePaaVegneAvPrivatperson(request: OpprettServiceklageRequest, paloggetBruker: String?) {
        hasText(request.innmelder?.navn, "innmelder.navn", " dersom paaVegneAv=PRIVATPERSON")
        hasText(request.innmelder?.personnummer, "innmelder.personnummer", " dersom paaVegneAv=PRIVATPERSON")
        isNotNull(request.oenskerAaKontaktes, "oenskerAaKontaktes", " dersom paaVegneAv=PRIVATPERSON")
        if (request.oenskerAaKontaktes == true) {
            hasText(request.innmelder?.telefonnummer, "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true")
        }
        request.innmelder?.personnummer?.let { validateFnr(it) }
        validateRequestFnrMatchesTokenFnr(request.innmelder?.personnummer, paloggetBruker)
    }

    private fun validatePaaVegneAvAnnenPerson(request: OpprettServiceklageRequest) {
        hasText(request.innmelder?.navn, "innmelder.navn", " dersom paaVegneAv=ANNEN_PERSON")
        hasText(request.innmelder?.rolle, "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON")
        isNotNull(request.innmelder?.harFullmakt, "innmelder.harFullmakt", " dersom paaVegneAv=ANNEN_PERSON")
        if (request.innmelder?.harFullmakt == false) {
            isNull(
                request.oenskerAaKontaktes,
                "oenskerAaKontaktes",
                " dersom klagen er meldt inn på vegne av annen person uten fullmakt"
            )
        }
        if (request.oenskerAaKontaktes != null && request.oenskerAaKontaktes == true) {
            hasText(request.innmelder?.telefonnummer, "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true")
        }
        isNotNull(request.paaVegneAvPerson, "paaVegneAvPerson", " dersom paaVegneAv=ANNEN_PERSON")
        hasText(request.paaVegneAvPerson?.navn, "paaVegneAvPerson.navn")
        hasText(request.paaVegneAvPerson?.personnummer, "paaVegneAvPerson.personnummer")
        request.paaVegneAvPerson?.personnummer?.let { validateFnr(it) }
    }

    private fun validatePaaVegneAvBedrift(request: OpprettServiceklageRequest) {
        isNotNull(request.paaVegneAvBedrift, "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT")
        hasText(request.paaVegneAvBedrift?.navn, "paaVegneAvBedrift.navn")
        hasText(request.paaVegneAvBedrift?.organisasjonsnummer, "paaVegneAvBedrift.organisasjonsnummer")
        isNotNull(request.oenskerAaKontaktes, "oenskerAaKontaktes", " dersom paaVegneAv=BEDRIFT")
        hasText(request.enhetsnummerPaaklaget, "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT")
        if (!StringUtils.isNumeric(request.enhetsnummerPaaklaget) && request.enhetsnummerPaaklaget?.length != ENHETSNUMMER_LENGTH) {
            throw ClientErrorException("enhetsnummerPaaklaget må ha fire siffer")
        }
        hasText(request.enhetsnummerPaaklaget, "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT")
        if (request.oenskerAaKontaktes == true) {
            hasText(request.innmelder?.navn, "innmelder.navn", " dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true")
            hasText(request.innmelder?.telefonnummer, "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true")
        }
        request.paaVegneAvBedrift?.organisasjonsnummer?.let { validateOrgnr(it) }
    }

    private fun validateFnr(fnr: String) {
        personnummerValidator.validate(fnr)
        pdlService.hentAktorIdForIdent(fnr)
    }

    private fun validateOrgnr(orgnr: String) {
        eregConsumer.hentInfo(orgnr)
    }

    private fun validateRequestFnrMatchesTokenFnr(fnr: String?, paloggetBruker: String?) {
        if (paloggetBruker != null && fnr != paloggetBruker) {
            throw ClientErrorException("innmelder.personnummer samsvarer ikke med brukertoken")
        }
    }
}
