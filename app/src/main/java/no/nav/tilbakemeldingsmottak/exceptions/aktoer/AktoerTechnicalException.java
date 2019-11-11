package no.nav.tilbakemeldingsmottak.exceptions.aktoer;

import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;

/**
 * Thrown to indicate that a method has been passed an invalid/illegal or inappropriate argument.
 *
 * @author Stig Kleppe-Jørgensen
 * @author Mette Lafton
 */
public class AktoerTechnicalException extends AbstractTilbakemeldingsmottakTechnicalException {
	public AktoerTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}

}