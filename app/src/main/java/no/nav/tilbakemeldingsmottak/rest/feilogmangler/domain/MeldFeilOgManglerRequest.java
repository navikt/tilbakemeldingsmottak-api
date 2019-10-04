package no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeldFeilOgManglerRequest {
    private String navn;
    private String telefonnummer;
    private Feiltype feiltype;
    private String melding;
}
