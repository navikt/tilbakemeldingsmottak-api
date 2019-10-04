package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Innmelder {
    private String navn;
    private String telefonnummer;
    private String personnummer;
    private Boolean harFullmakt;
    private String rolle;
}
