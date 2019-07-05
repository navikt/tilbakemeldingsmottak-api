package no.nav.tilbakemeldingsmottak.consumer.joark.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Bruker {
    private BrukerIdType idType;
    private String id;
}
