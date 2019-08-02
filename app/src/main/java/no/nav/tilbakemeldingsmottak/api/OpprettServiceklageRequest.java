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
public class OpprettServiceklageRequest {
    private PaaVegneAvType paaVegneAv;
    private Innmelder innmelder;
    private PaaVegneAvPerson paaVegneAvPerson;
    private PaaVegneAvBedrift paaVegneAvBedrift;
    private Klagetype klagetype;
    private String klagetekst;
    private Boolean oenskerAaKontaktes;
}
