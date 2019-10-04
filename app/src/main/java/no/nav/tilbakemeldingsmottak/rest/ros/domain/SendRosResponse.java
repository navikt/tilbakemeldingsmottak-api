package no.nav.tilbakemeldingsmottak.rest.ros.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendRosResponse {
    String message;
}
