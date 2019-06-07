package no.nav.serviceklagemottak.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OpprettServiceklageRequest {

    private PaaVegneAvType paaVegneAv;
    private Innmelder innmelder;
    private PaaVegneAvPerson paaVegneAvPerson;
    private PaaVegneAvBedrift paaVegneAvBedrift;
    private String klagetype;
    private String klagetekst;
    private Boolean oenskerAaKontaktes;

}
