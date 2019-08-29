package no.nav.tilbakemeldingsmottak.validators;

import no.nav.tilbakemeldingsmottak.api.Innmelder;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvBedrift;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class OpprettServiceklageValidator implements RequestValidator {

    public void validateRequest(OpprettServiceklageRequest request) {
        isNotNull(request.getKlagetype(), "klagetype");
        hasText(request.getKlagetekst(), "klagetekst");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes");
        isNotNull(request.getPaaVegneAv(), "paaVegneAv");

        validateInnmelder(request.getInnmelder(), request.getPaaVegneAv(), request.getOenskerAaKontaktes());
        if (request.getPaaVegneAv() == PaaVegneAvType.ANNEN_PERSON) {
            validatePaaVegneAvPerson(request.getPaaVegneAvPerson());
        }
        if (request.getPaaVegneAv() == PaaVegneAvType.BEDRIFT) {
            validatePaaVegneAvBedrift(request.getPaaVegneAvBedrift());
        }
    }

    private void validateInnmelder(Innmelder innmelder, PaaVegneAvType paaVegneAv, boolean oenskerAaKontaktes) {
        isNotNull(innmelder, "innmelder");

        if (PaaVegneAvType.PRIVATPERSON.equals(paaVegneAv)
                && StringUtils.isNotBlank(MDC.get(MDCConstants.MDC_USER_ID))
                && !innmelder.getPersonnummer().equals(MDC.get(MDCConstants.MDC_USER_ID))) {
            throw new InvalidRequestException("innmelder.personnummer samsvarer ikke med brukertoken");
        }

        hasText(innmelder.getNavn(), "innmelder.navn");
        if (oenskerAaKontaktes) {
            hasText(innmelder.getTelefonnummer(), "innmelder.telefonnummer", "dersom oenskerAaKontaktes=true");
        }
        if (paaVegneAv == PaaVegneAvType.PRIVATPERSON) {
            hasText(innmelder.getPersonnummer(), "innmelder.personnummer", " dersom paaVegneAv=PRIVATPERSON");
        }
        if (paaVegneAv == PaaVegneAvType.ANNEN_PERSON) {
            isNotNull(innmelder.getHarFullmakt(), "innmelder.harFullmakt", " dersom paaVegneAv=ANNEN_PERSON");
        }
        if (paaVegneAv == PaaVegneAvType.ANNEN_PERSON || paaVegneAv == PaaVegneAvType.BEDRIFT) {
            hasText(innmelder.getRolle(), "innmelder.rolle", " dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");
        }
    }

    private void validatePaaVegneAvPerson(PaaVegneAvPerson paaVegneAvPerson) {
        isNotNull(paaVegneAvPerson, "paaVegneAvPerson", " dersom paaVegneAv=ANNEN_PERSON");
        hasText(paaVegneAvPerson.getNavn(), "paaVegneAvPerson.navn");
        hasText(paaVegneAvPerson.getPersonnummer(), "paaVegneAvPerson.personnummer");
    }

    private void validatePaaVegneAvBedrift(PaaVegneAvBedrift paaVegneAvBedrift) {
        isNotNull(paaVegneAvBedrift, "paaVegneAvBedrift", " dersom paaVegneAv=BEDRIFT");
        hasText(paaVegneAvBedrift.getNavn(), "paaVegneAvBedrift.navn");
        hasText(paaVegneAvBedrift.getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        hasText(paaVegneAvBedrift.getPostadresse(), "paaVegneAvBedrift.postadresse");
        hasText(paaVegneAvBedrift.getTelefonnummer(), "paaVegneAvBedrift.telefonnummer");
    }
}
