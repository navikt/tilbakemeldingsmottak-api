package no.nav.tilbakemeldingsmottak.exceptions;

/**
 * Thrown to indicate that a method has been passed an invalid/illegal or inappropriate argument.
 *
 * @author Stig Kleppe-Jørgensen
 * @author Mette Lafton
 */
public class AktoerTechnicalException extends TilbakemeldingsmottakTechnicalException {
	public AktoerTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}

}