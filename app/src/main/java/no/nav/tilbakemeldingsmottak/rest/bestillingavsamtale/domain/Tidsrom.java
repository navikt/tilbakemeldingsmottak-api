package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain;

public enum Tidsrom {
    FORMIDDAG ("8.00-10.00"),
    ETTERMIDDAG ("13.30-15.30");

    public final String text;

    Tidsrom(String text) {
        this.text = text;
    }
}
