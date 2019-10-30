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
        isNotNull(request.getPaaVegneAv(), "paaVegneAv");
        isNotNull(request.getKlagetype(), "klagetype");
        isNotNull(request.getInnmelder(), "innmelder");
        if (LOKALT_NAV_KONTOR.equals(request.getKlagetype())) {
            isNotNull(request.getGjelderSosialhjelp(), "gjelderSosialhjelp", " dersom klagetype=LOKALT_NAV_KONTOR");
        }
        hasText(request.getKlagetekst(), "klagetekst");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes");
        hasText(request.getInnmelder().getNavn(), "innmelder.navn");
        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

    }

    private void validatePaaVegneAvPrivatperson(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getPersonnummer(), "innmelder.personnummer", " dersom paaVegneAv=PRIVATPERSON");

        if (StringUtils.isNotBlank(MDC.get(MDCConstants.MDC_USER_ID))
                && !request.getInnmelder().getPersonnummer().equals(MDC.get(MDCConstants.MDC_USER_ID))) {
            throw new InvalidRequestException("innmelder.personnummer samsvarer ikke med brukertoken");
        }
    }

    private void validatePaaVegneAvAnnenPerson(OpprettServiceklageRequest request) {
        isNotNull(request.getInnmelder().getHarFullmakt(), "innmelder.harFullmakt", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");

        isNotNull(request.getPaaVegneAvPerson(), "paaVegneAvPerson", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getPaaVegneAvPerson().getNavn(), "paaVegneAvPerson.navn");
        hasText(request.getPaaVegneAvPerson().getPersonnummer(), "paaVegneAvPerson.personnummer");
    }

    private void validatePaaVegneAvBedrift(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");

        isNotNull(request.getPaaVegneAvBedrift(), "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getPaaVegneAvBedrift().getNavn(), "paaVegneAvBedrift.navn");
        hasText(request.getPaaVegneAvBedrift().getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        hasText(request.getPaaVegneAvBedrift().getPostadresse(), "paaVegneAvBedrift.postadresse");
        hasText(request.getPaaVegneAvBedrift().getTelefonnummer(), "paaVegneAvBedrift.telefonnummer");
    }
}
