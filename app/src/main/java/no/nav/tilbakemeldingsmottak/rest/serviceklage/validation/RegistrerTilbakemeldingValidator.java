package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.RegistrerTilbakemeldingRequest;

public class RegistrerTilbakemeldingValidator implements RequestValidator {

    private static final String DERSOM_SERVICEKLAGE = " dersom klagen er en serviceklage";
    private static final String DERSOM_IKKE_SERVICEKLAGE = " dersom klagen ikke er en serviceklage";

    public void validateRequest(RegistrerTilbakemeldingRequest request) {
        hasText(request.getErServiceklage(), "erServiceklage");
        if(request.getErServiceklage().contains("Ja")) {
            hasText(request.getKanal(), "kanal", DERSOM_SERVICEKLAGE);
            hasText(request.getPaaklagetEnhet(), "paaklagetEnhet", DERSOM_SERVICEKLAGE);
            hasText(request.getBehandlendeEnhet(), "behandlendeEnhet", DERSOM_SERVICEKLAGE);
            hasText(request.getYtelseTjeneste(), "ytelseTjeneste", DERSOM_SERVICEKLAGE);
            hasText(request.getTema(), "tema", DERSOM_SERVICEKLAGE);
            hasText(request.getUtfall(), "utfall", DERSOM_SERVICEKLAGE);
            isNotNull(request.getSvarmetode(), "svarmetode", DERSOM_SERVICEKLAGE);

        } else {
            hasText(request.getGjelder(), "gjelder", DERSOM_IKKE_SERVICEKLAGE);
        }
    }

}
