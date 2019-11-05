package no.nav.tilbakemeldingsmottak.exceptions.sts;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;

/**
 * @author Sigurd Midttun, Visma Consulting
 */
public class StsFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {

	public StsFunctionalException(String message, Throwable cause) {
		super(message, cause);
	}

	public StsFunctionalException(String message) {
		super(message);
	}
}
