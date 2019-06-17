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
        if (request.getPaaVegneAv() == PaaVegneAvType.ANNEN_PERSON) {
            validatePaaVegneAvPerson(request.getPaaVegneAvPerson());
        }
        if (request.getPaaVegneAv() == PaaVegneAvType.BEDRIFT) {
            validatePaaVegneAvBedrift(request.getPaaVegneAvBedrift());
        }
    }

    private void validateInnmelder(Innmelder innmelder, PaaVegneAvType paaVegneAv) {
        isNotNull(innmelder, "innmelder");
        hasText(innmelder.getNavn(), "innmelder.navn");
        hasText(innmelder.getTelefonnummer(), "innmelder.telefonnummer");
        if (paaVegneAv == PaaVegneAvType.PRIVATPERSON) {
            hasText(innmelder.getPersonnummer(), "innmelder.personnummer", "dersom paaVegneAv=PRIVATPERSON");
        }
        if (paaVegneAv == PaaVegneAvType.ANNEN_PERSON) {
            isNotNull(innmelder.getHarFullmakt(), "innmelder.harFullmakt", "dersom paaVegneAv=ANNEN_PERSON");
        }
        if (paaVegneAv == PaaVegneAvType.ANNEN_PERSON || paaVegneAv == PaaVegneAvType.BEDRIFT) {
            hasText(innmelder.getRolle(), "innmelder.rolle", "dersom paaVegneAv=ANNEN_PERSON eller paaVegneAv=BEDRIFT");
        }
    }

    private void validatePaaVegneAvPerson(PaaVegneAvPerson paaVegneAvPerson) {
        isNotNull(paaVegneAvPerson, "paaVegneAvPerson", "dersom paaVegneAv=ANNEN_PERSON");
        hasText(paaVegneAvPerson.getNavn(), "paaVegneAvPerson.navn");
        hasText(paaVegneAvPerson.getPersonnummer(), "paaVegneAvPerson.personnummer");
    }

    private void validatePaaVegneAvBedrift(PaaVegneAvBedrift paaVegneAvBedrift) {
        isNotNull(paaVegneAvBedrift, "paaVegneAvBedrift", "dersom paaVegneAv=BEDRIFT");
        hasText(paaVegneAvBedrift.getNavn(), "paaVegneAvBedrift.navn");
        hasText(paaVegneAvBedrift.getOrganisasjonsnummer(), "paaVegneAvBedrift.organisasjonsnummer");
        hasText(paaVegneAvBedrift.getPostadresse(), "paaVegneAvBedrift.postadresse");
        hasText(paaVegneAvBedrift.getTelefonnummer(), "paaVegneAvBedrift.telefonnummer");
    }
}
