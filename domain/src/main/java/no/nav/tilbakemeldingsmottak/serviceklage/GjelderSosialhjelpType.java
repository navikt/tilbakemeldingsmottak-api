package no.nav.tilbakemeldingsmottak.serviceklage;

public enum GjelderSosialhjelpType {
    JA ("Ja"),
    NEI ("Nei"),
    VET_IKKE ("Vet ikke");

    public final String text;

    GjelderSosialhjelpType(String text) {
        this.text = text;
    }
}
