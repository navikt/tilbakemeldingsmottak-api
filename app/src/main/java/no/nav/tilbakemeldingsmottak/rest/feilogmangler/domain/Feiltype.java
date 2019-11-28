package no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain;

public enum Feiltype {
    TEKNISK_FEIL ("Teknisk feil"),
    FEIL_INFO ("Feil informasjon"),
    UNIVERSELL_UTFORMING ("Feil på siden ved bruk av hjelpemiddelteknologi");

    public final String text;

    Feiltype(String text) {
        this.text = text;
    }
}
