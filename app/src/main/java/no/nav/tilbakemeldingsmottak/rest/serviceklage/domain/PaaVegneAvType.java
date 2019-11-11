package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

public enum PaaVegneAvType {
    PRIVATPERSON ("Bruker selv som privatperson"),
    ANNEN_PERSON ("På vegne av en annen privatperson"),
    BEDRIFT ("På vegne av virksomhet");

    public final String text;

    PaaVegneAvType(String text) {
        this.text = text;
    }
}
