package no.nav.tilbakemeldingsmottak.rest.common.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    String message;
}
