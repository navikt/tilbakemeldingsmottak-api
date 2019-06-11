package no.nav.serviceklagemottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaaVegneAvBedrift {

    private String navn;
    private String organisasjonsnummer;
    private String postadresse;
    private String telefonnummer;

}