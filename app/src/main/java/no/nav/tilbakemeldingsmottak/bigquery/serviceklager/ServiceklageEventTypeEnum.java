package no.nav.tilbakemeldingsmottak.bigquery.serviceklager;


public enum ServiceklageEventTypeEnum {
    OPPRETT_SERVICEKLAGE("OPPRETT_SERVICEKLAGE"),
    KLASSIFISER_SERVICEKLAGE("KLASSIFISER_SERVICEKLAGE");

    public final String value;

    ServiceklageEventTypeEnum(String value) {
        this.value = value;
    }
}
