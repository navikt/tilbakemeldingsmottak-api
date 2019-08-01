package no.nav.tilbakemeldingsmottak.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendRosResponse {
    String message;
}
