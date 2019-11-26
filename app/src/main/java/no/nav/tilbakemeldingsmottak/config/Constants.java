package no.nav.tilbakemeldingsmottak.config;

public final class Constants {
	private Constants() {}

	public static final String AZURE_ISSUER = "azuread"; // sluttbruker på nav.no
	public static final String RESTSTS_ISSUER = "reststs";
	public static final String DOKLOGIN_ISSUER = "azure"; // saksbehandler
	public static final String REDIRECT_COOKIE = "redirect_cookie";
}
