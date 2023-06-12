package no.nav.tilbakemeldingsmottak.rest.common.domain;

import lombok.Builder;
import lombok.Data;

// Message er for utviklere
// ErrorCode er for frontend som skal mappe koden til en brukervennlig tekst
@Data
@Builder
public class ErrorResponse {
    String message;
    String errorCode;
}
