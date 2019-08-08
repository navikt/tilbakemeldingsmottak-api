package no.nav.tilbakemeldingsmottak.api;

public enum HvemRosesType {
    NAV_KONTAKTSENTER ("NAV Kontaktsenter"),
    NAV_DIGITALE_TJENESTER ("NAVs digitale tjenester"),
    NAV_KONTOR ("NAV-kontor");

    public final String text;

    HvemRosesType(String text) {
        this.text = text;
    }
}
