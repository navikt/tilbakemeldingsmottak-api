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
public class PaaVegneAvBedrift {
    private String navn;
    private String organisasjonsnummer;
    private String postadresse;
    private String telefonnummer;
}