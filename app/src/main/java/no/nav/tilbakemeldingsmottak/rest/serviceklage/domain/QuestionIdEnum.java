package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

public enum QuestionIdEnum {
    BEHANDLES_SOM_SERVICEKLAGE ("Skal klagen behandles etter serviceklagerutinen?"),
    BEHANDLES_SOM_SERVICEKLAGE_SPESIFISER ("Spesifiser"),
    FREMMET_DATO ("Angi dato bruker fremmet serviceklagen"),
    INNSENDER ("Hvem sendte inn serviceklagen?"),
    KANAL ("Angi kanal for serviceklagen"),
    PAAKLAGET_ENHET_ER_BEHANDLENDE ("Er klagen behandlet i enheten det klages på?"),
    ENHETSNUMMER_PAAKLAGET ("Angi enhetsnummer til enheten det klages på"),
    ENHETSNUMMER_BEHANDLENDE ("Angi enhetsnummer til enheten som behandler klagen"),
    GJELDER ("Hva mener du serviceklagen gjelder?"),
    YTELSE ("Hvilken ytelse mm. er mest relevant for serviceklagen?"),
    TEMA ("Hva gjelder serviceklagen?"),
    VENTE ("Vente på NAV"),
    TILGJENGELIGHET ("Tilgjengelighet"),
    INFORMASJON ("Informasjon"),
    VEILEDNING ("Veiledning og oppfølging mot arbeid"),
    TEMA_SPESIFISER ("Spesifiser"),
    UTFALL ("Hva er utfallet av serviceklagen?"),
    AARSAK ("Hva er, etter din mening, de bakenforliggende årsakene?"),
    TILTAK ("Hvordan kan NAV hindre at det samme skjer igjen, etter din mening?"),
    SVARMETODE ("Hvordan har bruker blitt svart?"),
    SVAR_IKKE_NOEDVENDIG("Svar ikke nødvendig"),
    SVARMETODE_SPESIFISER ("Spesifiser"),
    FERDIG_DATO("Angi dato serviceklagen var ferdigbehandlet og avsluttet i Gosys");

    public final String text;

    QuestionIdEnum(String text) {
        this.text = text;
    }
}
