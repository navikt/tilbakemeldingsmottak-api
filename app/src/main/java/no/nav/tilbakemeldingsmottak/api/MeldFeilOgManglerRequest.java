package no.nav.tilbakemeldingsmottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeldFeilOgManglerRequest {
    private String navn;
    private String telefonnummer;
    private String feiltype;
    private String melding;
}
