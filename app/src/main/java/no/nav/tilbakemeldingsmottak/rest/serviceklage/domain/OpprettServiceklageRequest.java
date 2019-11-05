package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpprettServiceklageRequest {
    private PaaVegneAvType paaVegneAv;
    private Innmelder innmelder;
    private PaaVegneAvPerson paaVegneAvPerson;
    private PaaVegneAvBedrift paaVegneAvBedrift;
    private String enhetsnummerPaaklaget;
    private List<Klagetype> klagetyper;
    private GjelderSosialhjelpType gjelderSosialhjelp;
    private String klagetekst;
    private Boolean oenskerAaKontaktes;
}
