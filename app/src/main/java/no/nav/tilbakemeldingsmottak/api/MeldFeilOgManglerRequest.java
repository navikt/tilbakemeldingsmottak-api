package no.nav.tilbakemeldingsmottak.api;

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
