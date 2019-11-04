package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype.LOKALT_NAV_KONTOR;

import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class OpprettServiceklageValidator implements RequestValidator {

    public void validateRequest(OpprettServiceklageRequest request) {
        validateCommonRequiredFields(request);

        switch(request.getPaaVegneAv()) {
            case PRIVATPERSON:
                validatePaaVegneAvPrivatperson(request);
                break;
            case ANNEN_PERSON:
                validatePaaVegneAvAnnenPerson(request);
                break;
            case BEDRIFT:
                validatePaaVegneAvBedrift(request);
                break;
        }
    }

    private void validateCommonRequiredFields(OpprettServiceklageRequest request) {
        isNotNull(request.getKlagetyper(), "klagetyper");
        if (request.getKlagetyper().contains(LOKALT_NAV_KONTOR)) {
            isNotNull(request.getGjelderSosialhjelp(), "gjelderSosialhjelp", " dersom klagetyper=LOKALT_NAV_KONTOR");
        }
        isNotNull(request.getPaaVegneAv(), "paaVegneAv");
        isNotNull(request.getInnmelder(), "innmelder");
        hasText(request.getKlagetekst(), "klagetekst");
    }

    private void validatePaaVegneAvPrivatperson(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=PRIVATPERSON");
        hasText(request.getInnmelder().getPersonnummer(), "innmelder.personnummer", " dersom paaVegneAv=PRIVATPERSON");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom paaVegneAv=PRIVATPERSON");
        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        if (StringUtils.isNotBlank(MDC.get(MDCConstants.MDC_USER_ID))
                && !request.getInnmelder().getPersonnummer().equals(MDC.get(MDCConstants.MDC_USER_ID))) {
            throw new InvalidRequestException("innmelder.personnummer samsvarer ikke med brukertoken");
        }
    }

    private void validatePaaVegneAvAnnenPerson(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");
        isNotNull(request.getInnmelder().getHarFullmakt(), "innmelder.harFullmakt", " dersom paaVegneAv=ANNEN_PERSON");
        if (!request.getInnmelder().getHarFullmakt()) {
            isNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom klagen er meldt inn på vegne av annen person uten fullmakt");
        }
        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        isNotNull(request.getPaaVegneAvPerson(), "paaVegneAvPerson", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getPaaVegneAvPerson().getNavn(), "paaVegneAvPerson.navn");
        hasText(request.getPaaVegneAvPerson().getPersonnummer(), "paaVegneAvPerson.personnummer");
    }

    private void validatePaaVegneAvBedrift(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");

        isNotNull(request.getPaaVegneAvBedrift(), "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getPaaVegneAvBedrift().getNavn(), "paaVegneAvBedrift.navn");
        hasText(request.getPaaVegneAvBedrift().getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom paaVegneAv=PRIVATPERSON");

        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true");
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }
    }
}
