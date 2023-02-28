package no.nav.tilbakemeldingsmottak.serviceklage;

public enum Klagetype {
    TELEFON ("Telefon"),
    LOKALT_NAV_KONTOR ("Lokalt NAV-kontor"),
    NAV_DIGITALE_TJENESTER("NAVs digitale tjenester"),
    BREV ("Brev"),
    ANNET ("Annet");

    public final String text;

    Klagetype(String text) {
        this.text = text;
    }
}