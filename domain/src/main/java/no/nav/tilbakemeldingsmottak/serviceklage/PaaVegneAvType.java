package no.nav.tilbakemeldingsmottak.serviceklage;

public enum PaaVegneAvType {
    PRIVATPERSON ("Bruker selv som privatperson"),
    ANNEN_PERSON ("På vegne av en annen privatperson"),
    BEDRIFT ("På vegne av virksomhet");

    public final String text;

    PaaVegneAvType(String text) {
        this.text = text;
    }
}
