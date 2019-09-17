package no.nav.tilbakemeldingsmottak.api;

public enum Feiltype {
    TEKNISK_FEIL ("Teknisk feil"),
    FEIL_INFO ("Feil informasjon"),
    UNIVERSELL_UTFORMING ("Lav grad av universell utforming");

    public final String text;

    Feiltype(String text) {
        this.text = text;
    }
}
