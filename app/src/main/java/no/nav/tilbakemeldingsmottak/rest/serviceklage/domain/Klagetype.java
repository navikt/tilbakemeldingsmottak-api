package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

public enum Klagetype {
    SAKSBEHANDLING ("Saksbehandling av søknad"),
    NAV_KONTOR ("NAV-kontor"),
    TELEFON ("Telefon"),
    NAVNO ("nav.no"),
    ANNET ("Annet");

    public final String text;

    Klagetype(String text) {
        this.text = text;
    }
}
