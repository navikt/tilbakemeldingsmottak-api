package no.nav.tilbakemeldingsmottak.bigquery.serviceklager;


public enum ServiceklageEventTypeEnum {
    OPPRETT_SERVICEKLAGE("OPPRETT_SERVICEKLAGE"), // Ved opprettelse av serviceklage
    KLASSIFISER_SERVICEKLAGE("KLASSIFISER_SERVICEKLAGE"), // Ved klassifisering av serviceklage
    OPPDATER_SERVICEKLAGE("OPPDATER_SERVICEKLAGE"); // Ved oppdatering av serviceklage (feks ved feilregistrering eller ved manglende felt)


    public final String value;

    ServiceklageEventTypeEnum(String value) {
        this.value = value;
    }
}
