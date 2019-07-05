package no.nav.tilbakemeldingsmottak.consumer.joark.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Sak {
    private String arkivsaksnummer;
    private Arkivsaksystem arkivsaksystem;
}
