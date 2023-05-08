package no.nav.tilbakemeldingsmottak.consumer.joark.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Tilleggsopplysning {
    private String nokkel;
    private String verdi;
}