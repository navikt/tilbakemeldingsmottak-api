package no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeldFeilOgManglerRequest {
    private Boolean onskerKontakt;
    private String epost;
    private Feiltype feiltype;
    private String melding;
}
