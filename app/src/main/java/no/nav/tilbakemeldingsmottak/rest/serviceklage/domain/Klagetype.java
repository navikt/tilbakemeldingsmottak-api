package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

public enum Klagetype {
    TELEFON ("Telefon"),
    LOKALT_NAV_KONTOR ("Lokalt NAV-kontor"),
    NAVNO ("NAV.no"),
    SKRIFTLIG_KONTAKT_NETT ("Chat, meldinger, og annen skriftlig kontakt på nettsiden"),
    BREV ("Brev"),
    FLERE_KATEGORIER ("Gjelder flere kategorier"),
    ANNET ("Annet");

    public final String text;

    Klagetype(String text) {
        this.text = text;
    }
}
