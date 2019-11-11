package no.nav.tilbakemeldingsmottak.rest.ros.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendRosRequest {
    private HvemRosesType hvemRoses;
    private String navKontor;
    private String melding;
}
