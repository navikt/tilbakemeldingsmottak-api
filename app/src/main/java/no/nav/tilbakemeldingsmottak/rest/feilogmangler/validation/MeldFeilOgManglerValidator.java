package no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation;

import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import org.springframework.stereotype.Component;

@Component
public class MeldFeilOgManglerValidator extends RequestValidator {

    public void validateRequest(MeldFeilOgManglerRequest request) {
        isNotNull(request.getOnskerKontakt(), "onskerKontakt");
        if (request.getOnskerKontakt()) {
            hasText(request.getEpost(), "epost", " dersom onskerKontakt=true");
        }
        isNotNull(request.getFeiltype(), "feiltype");
        hasText(request.getMelding(), "melding");
    }

}
