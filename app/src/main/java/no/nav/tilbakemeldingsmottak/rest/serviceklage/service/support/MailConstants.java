package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

public class MailConstants {
    public static final String SUBJECT_KOMMUNAL_KLAGE = "Kommunal klage mottatt via serviceklageskjema på nav.no";
    public static final String TEXT_KOMMUNAL_KLAGE = "En kommunal klage har blitt sendt inn via serviceklageskjema på nav.no. Denne har ikke blitt journalført eller lagret i serviceklagedatabasen. Feilsendt klage ligger vedlagt.";

    public static final String SUBJECT_JOURNALPOST_FEILET = "Automatisk journalføring av serviceklage feilet";
    public static final String TEXT_JOURNALPOST_FEILET= "Manuell journalføring og opprettelse av oppgave kreves. Klagen ligger vedlagt.";

    public static final String SUBJECT_OPPGAVE_FEILET = "Automatisk opprettelse av oppgave feilet";
    public static final String TEXT_OPPGAVE_FEILET= "Manuell opprettelse av oppgave kreves for serviceklage med journalpostId=";
}
