package no.nav.tilbakemeldingsmottak.exceptions.sts;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

/**
 * @author Sigurd Midttun, Visma Consulting
 */
public class StsTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {

	public StsTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
