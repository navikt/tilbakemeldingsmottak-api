package no.nav.tilbakemeldingsmottak.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendRosRequest {
    private String navn;
    private String telefonnummer;
    private HvemRosesType hvemRoses;
    private String navKontor;
    private String melding;
}
