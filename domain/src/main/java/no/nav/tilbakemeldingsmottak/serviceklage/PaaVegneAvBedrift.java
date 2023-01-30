package no.nav.tilbakemeldingsmottak.serviceklage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaaVegneAvBedrift {
    private String navn;
    private String organisasjonsnummer;
}