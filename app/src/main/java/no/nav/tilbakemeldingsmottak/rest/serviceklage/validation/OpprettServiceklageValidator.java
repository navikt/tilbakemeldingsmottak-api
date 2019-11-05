package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype.LOKALT_NAV_KONTOR;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.consumer.aktoer.AktoerConsumer;
import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.exceptions.aktoer.AktoerTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregTechnicalException;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpprettServiceklageValidator implements RequestValidator {

    private final EregConsumer eregConsumer;
    private final AktoerConsumer aktoerConsumer;

    private static final int ENHETSNUMMER_LENGTH = 4;

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

        validateFnr(request.getInnmelder().getPersonnummer());
    }

    private void validatePaaVegneAvAnnenPerson(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");
        isNotNull(request.getInnmelder().getHarFullmakt(), "innmelder.harFullmakt", " dersom paaVegneAv=ANNEN_PERSON");
        if (!request.getInnmelder().getHarFullmakt()) {
            isNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom klagen er meldt inn på vegne av annen person uten fullmakt");
        }
        if (request.getOenskerAaKontaktes() != null && request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        isNotNull(request.getPaaVegneAvPerson(), "paaVegneAvPerson", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getPaaVegneAvPerson().getNavn(), "paaVegneAvPerson.navn");
        hasText(request.getPaaVegneAvPerson().getPersonnummer(), "paaVegneAvPerson.personnummer");

        validateFnr(request.getPaaVegneAvPerson().getPersonnummer());
    }

    private void validatePaaVegneAvBedrift(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");

        isNotNull(request.getPaaVegneAvBedrift(), "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getPaaVegneAvBedrift().getNavn(), "paaVegneAvBedrift.navn");
        hasText(request.getPaaVegneAvBedrift().getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getEnhetsnummerPaaklaget(), "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT");
        if(!isNumeric(request.getEnhetsnummerPaaklaget()) && request.getEnhetsnummerPaaklaget().length() != ENHETSNUMMER_LENGTH) {
            throw new InvalidRequestException("enhetsnummerPaaklaget må ha fire siffer");
        }
        hasText(request.getEnhetsnummerPaaklaget(), "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT");

        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true");
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        validateOrgnr(request.getPaaVegneAvBedrift().getOrganisasjonsnummer());
    }

    private void validateFnr(String fnr) {
        try {
            aktoerConsumer.hentAktoerIdForIdent(fnr);
        } catch (AktoerTechnicalException e) {
            throw new InvalidRequestException("Ugyldig personnummer: " + fnr);
        }
    }

    private void validateOrgnr(String orgnr) {
        try {
            eregConsumer.hentInfo(orgnr);
        } catch (EregFunctionalException | EregTechnicalException e) {
            throw new InvalidRequestException("Ugyldig organisasjonsnummer: " + orgnr);
        }
    }
}
