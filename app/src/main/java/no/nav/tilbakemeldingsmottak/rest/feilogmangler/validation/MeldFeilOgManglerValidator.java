package no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation;

import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;

public class MeldFeilOgManglerValidator implements RequestValidator {

    public void validateRequest(MeldFeilOgManglerRequest request) {
        hasText(request.getNavn(), "navn");
        isNotNull(request.getOnskerKontakt(), "onskerKontakt");
        if (request.getOnskerKontakt()) {
            hasText(request.getEpost(), "epost", " dersom onskerKontakt=true");
        }
        isNotNull(request.getFeiltype(), "feiltype");
        hasText(request.getMelding(), "melding");
    }

}
