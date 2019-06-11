package no.nav.serviceklagemottak.validators;

import no.nav.serviceklagemottak.api.Innmelder;
import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.api.PaaVegneAvBedrift;
import no.nav.serviceklagemottak.api.PaaVegneAvPerson;
import no.nav.serviceklagemottak.api.PaaVegneAvType;

public class OpprettServiceklageValidator extends AbstractValidator {

    public void validateRequest(OpprettServiceklageRequest request) {
        hasText(request.getKlagetype(), "klagetype");
        hasText(request.getKlagetekst(), "klagetekst");
        isNotNull(request.getOenskerAaKontaktes(), "oenskerAaKontaktes");

        validateInnmelder(request.getInnmelder(), request.getPaaVegneAv());
        if (request.getPaaVegneAv() == PaaVegneAvType.PERSON) {
            validatePaaVegneAvPerson(request.getPaaVegneAvPerson());
        }
        if (request.getPaaVegneAv() == PaaVegneAvType.BEDRIFT) {
            validatePaaVegneAvBedrift(request.getPaaVegneAvBedrift());
        }
    }

    private void validateInnmelder(Innmelder innmelder, PaaVegneAvType paaVegneAv) {
        isNotNull(innmelder, "innmelder");
        hasText(innmelder.getNavn(), "innmelder.navn");
        hasText(innmelder.getPersonnummer(), "innmelder.personnummer");
        if (paaVegneAv == null) {
            hasText(innmelder.getTelefonnummer(), "innmelder.telefonnummer", "dersom paaVegneAv ikke er satt");
        }
        if (paaVegneAv == PaaVegneAvType.PERSON) {
            isNotNull(innmelder.getHarFullmakt(), "innmelder.harFullmakt", "dersom paaVegneAv=person");
        }
        if (paaVegneAv == PaaVegneAvType.PERSON || paaVegneAv == PaaVegneAvType.BEDRIFT) {
            hasText(innmelder.getRolle(), "innmelder.rolle", "dersom paaVegneAv er satt");
        }
    }

    private void validatePaaVegneAvPerson(PaaVegneAvPerson paaVegneAvPerson) {
        isNotNull(paaVegneAvPerson, "paaVegneAvPerson", "dersom paaVegneAv=person");
        hasText(paaVegneAvPerson.getNavn(), "paaVegneAvPerson.navn");
        hasText(paaVegneAvPerson.getPersonnummer(), "paaVegneAvPerson.personnummer");
    }

    private void validatePaaVegneAvBedrift(PaaVegneAvBedrift paaVegneAvBedrift) {
        isNotNull(paaVegneAvBedrift, "paaVegneAvBedrift", "dersom paaVegneAv=bedrift");
        hasText(paaVegneAvBedrift.getNavn(), "paaVegneAvBedrift.navn");
        hasText(paaVegneAvBedrift.getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        hasText(paaVegneAvBedrift.getPostadresse(), "paaVegneAvBedrift.postadresse");
        hasText(paaVegneAvBedrift.getTelefonnummer(), "paaVegneAvBedrift.telefonnummer");
    }
}
