package no.nav.tilbakemeldingsmottak.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaaVegneAvBedrift {
    private String navn;
    private String organisasjonsnummer;
    private String postadresse;
    private String telefonnummer;
}