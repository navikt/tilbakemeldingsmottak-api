package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer;
import no.nav.tilbakemeldingsmottak.consumer.pdl.PdlService;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.rest.common.validation.PersonnummerValidator;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Component
@RequiredArgsConstructor
public class OpprettServiceklageValidator extends RequestValidator {

    private static final int ENHETSNUMMER_LENGTH = 4;
    private final EregConsumer eregConsumer;
    private final PersonnummerValidator personnummerValidator;
    private final PdlService pdlService;

    public void validateRequest(OpprettServiceklageRequest request) {
        validateRequest(request, Optional.empty());
    }

    public void validateRequest(OpprettServiceklageRequest request, Optional<String> paloggetBruker) {
        validateCommonRequiredFields(request);

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON -> validatePaaVegneAvPrivatperson(request, paloggetBruker);
            case ANNEN_PERSON -> validatePaaVegneAvAnnenPerson(request);
            case BEDRIFT -> validatePaaVegneAvBedrift(request);
        }
    }

    private void validateCommonRequiredFields(OpprettServiceklageRequest request) {
        isNotNull(request.getKlagetyper(), "klagetyper");
        if (request.getKlagetyper().contains(KlagetyperEnum.LOKALT_NAV_KONTOR)) {
            isNotNull(request.getGjelderSosialhjelp(), "gjelderSosialhjelp", " dersom klagetyper=LOKALT_NAV_KONTOR");
        }
        isNotNull(request.getPaaVegneAv(), "paaVegneAv");
        isNotNull(request.getInnmelder(), "innmelder");
        hasText(request.getKlagetekst(), "klagetekst");
    }

    private void validatePaaVegneAvPrivatperson(OpprettServiceklageRequest request, Optional<String> paloggetBruker) {
        hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=PRIVATPERSON");
        hasText(request.getInnmelder().getPersonnummer(), "innmelder.personnummer", " dersom paaVegneAv=PRIVATPERSON");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom paaVegneAv=PRIVATPERSON");
        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        validateFnr(request.getInnmelder().getPersonnummer());
        validateRequestFnrMatchesTokenFnr(request.getInnmelder().getPersonnummer(), paloggetBruker);
    }

    private void validatePaaVegneAvAnnenPerson(OpprettServiceklageRequest request) {
        hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(request.getInnmelder().getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON");
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
        isNotNull(request.getPaaVegneAvBedrift(), "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getPaaVegneAvBedrift().getNavn(), "paaVegneAvBedrift.navn");
        hasText(request.getPaaVegneAvBedrift().getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes", " dersom paaVegneAv=BEDRIFT");
        hasText(request.getEnhetsnummerPaaklaget(), "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT");
        if (!isNumeric(request.getEnhetsnummerPaaklaget()) && request.getEnhetsnummerPaaklaget().length() != ENHETSNUMMER_LENGTH) {
            throw new ClientErrorException("enhetsnummerPaaklaget må ha fire siffer");
        }
        hasText(request.getEnhetsnummerPaaklaget(), "enhetsnummerPaaklaget", " dersom paaVegneAv=BEDRIFT");

        if (request.getOenskerAaKontaktes()) {
            hasText(request.getInnmelder().getNavn(), "innmelder.navn", " dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true");
            hasText(request.getInnmelder().getTelefonnummer(), "innmelder.telefonnummer", " dersom oenskerAaKontaktes=true");
        }

        validateOrgnr(request.getPaaVegneAvBedrift().getOrganisasjonsnummer());
    }

    private void validateFnr(String fnr) {
        personnummerValidator.validate(fnr);
        pdlService.hentAktorIdForIdent(fnr);
    }

    private void validateOrgnr(String orgnr) {
        eregConsumer.hentInfo(orgnr);
    }

    private void validateRequestFnrMatchesTokenFnr(String fnr, Optional<String> paloggetBruker) {
        if (paloggetBruker.isPresent()
                && !fnr.equals(paloggetBruker.get())) {
            throw new ClientErrorException("innmelder.personnummer samsvarer ikke med brukertoken");
        }
    }
}
