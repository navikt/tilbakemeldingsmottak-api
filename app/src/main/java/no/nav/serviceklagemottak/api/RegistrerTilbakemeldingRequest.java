package no.nav.serviceklagemottak.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegistrerTilbakemeldingRequest {
    String tilbakemelding;
}
