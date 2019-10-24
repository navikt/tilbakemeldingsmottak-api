package no.nav.tilbakemeldingsmottak.exceptions.saf;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

public class MarshalGraphqlRequestToJsonTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {

	public MarshalGraphqlRequestToJsonTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
