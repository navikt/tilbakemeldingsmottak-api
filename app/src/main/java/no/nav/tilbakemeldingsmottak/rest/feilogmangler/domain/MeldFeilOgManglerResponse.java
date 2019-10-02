package no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeldFeilOgManglerResponse {
    String message;
}
