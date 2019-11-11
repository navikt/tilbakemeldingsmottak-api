package no.nav.tilbakemeldingsmottak.util;

import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ErrorResponseUtils {
	private ErrorResponseUtils() {}

	public static ResponseEntity<OpprettServiceklageResponse> createOpprettServiceklageErrorResponse(HttpStatus status, String feilmelding) {
		return ResponseEntity
				.status(status)
				.body(OpprettServiceklageResponse.builder()
						.message(feilmelding)
						.build()
				);
	}
}
