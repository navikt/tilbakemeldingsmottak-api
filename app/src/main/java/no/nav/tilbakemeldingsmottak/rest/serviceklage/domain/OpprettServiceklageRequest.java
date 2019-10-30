package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpprettServiceklageRequest {
    private PaaVegneAvType paaVegneAv;
    private Innmelder innmelder;
    private PaaVegneAvPerson paaVegneAvPerson;
    private PaaVegneAvBedrift paaVegneAvBedrift;
    private Klagetype klagetype;
    private GjelderSosialhjelpType gjelderSosialhjelp;
    private String klagetekst;
    private Boolean oenskerAaKontaktes;
}
