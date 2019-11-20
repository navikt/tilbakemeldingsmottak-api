package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

public class MailConstants {
    public static final String SUBJECT_KOMMUNAL_KLAGE = "Kommunal klage mottatt via serviceklageskjema på nav.no";
    public static final String TEXT_KOMMUNAL_KLAGE = "Feilsendt klage ligger vedlagt.";

    public static final String SUBJECT_JOURNALPOST_FEILET = "Automatisk journalføring av serviceklage feilet";
    public static final String TEXT_JOURNALPOST_FEILET= "Manuell opprettelse av journalpost og oppgave kreves da automatisk mottak av serviceklage feilet. Se innholdet i klagen i vedlagt pdf.";

    public static final String SUBJECT_OPPGAVE_FEILET = "Automatisk opprettelse av oppgave feilet";
    public static final String TEXT_OPPGAVE_FEILET= "Manuell opprettelse av oppgave kreves for serviceklage med journalpostId=";
}
