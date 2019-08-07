package no.nav.tilbakemeldingsmottak.consumer.joark.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AvsenderMottaker {
    private String id;
    private AvsenderMottakerIdType idType;
    private String navn;
    private String land;
}
