package no.nav.tilbakemeldingsmottak.exceptions.ereg;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;

/**
 * @author Sigurd Midttun, Visma Consulting
 */
public class EregFunctionalException extends AbstractTilbakemeldingsmottakFunctionalException {

	public EregFunctionalException(String message, Throwable cause) {
		super(message, cause);
	}

	public EregFunctionalException(String message) {
		super(message);
	}
}
