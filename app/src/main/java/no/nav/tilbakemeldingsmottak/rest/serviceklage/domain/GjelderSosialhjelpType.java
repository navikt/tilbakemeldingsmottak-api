package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

public enum GjelderSosialhjelpType {
    JA ("Ja"),
    NEI ("Nei"),
    VET_IKKE ("Vet ikke");

    public final String text;

    GjelderSosialhjelpType(String text) {
        this.text = text;
    }
}
