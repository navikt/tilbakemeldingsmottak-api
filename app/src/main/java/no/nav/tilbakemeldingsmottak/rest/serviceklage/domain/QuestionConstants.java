package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import java.util.HashMap;
import java.util.Map;

public final class QuestionConstants {
    public static final String BEHANDLES_SOM_SERVICEKLAGE = "BEHANDLES_SOM_SERVICEKLAGE";
    public static final String BEHANDLES_SOM_SERVICEKLAGE_SPESIFISER = "BEHANDLES_SOM_SERVICEKLAGE_SPESIFISER";
    public static final String FREMMET_DATO = "FREMMET_DATO";
    public static final String INNSENDER = "INNSENDER";
    public static final String KANAL = "KANAL";
    public static final String PAAKLAGET_ENHET_ER_BEHANDLENDE = "PAAKLAGET_ENHET_ER_BEHANDLENDE";
    public static final String ENHETSNUMMER_PAAKLAGET = "ENHETSNUMMER_PAAKLAGET";
    public static final String ENHETSNUMMER_BEHANDLENDE = "ENHETSNUMMER_BEHANDLENDE";
    public static final String GJELDER = "GJELDER";
    public static final String YTELSE = "YTELSE";
    public static final String TEMA = "TEMA";
    public static final String VENTE = "VENTE";
    public static final String TILGJENGELIGHET = "TILGJENGELIGHET";
    public static final String INFORMASJON = "INFORMASJON";
    public static final String VEILEDNING = "VEILEDNING";
    public static final String TEMA_SPESIFISER = "TEMA_SPESIFISER";
    public static final String UTFALL = "UTFALL";
    public static final String AARSAK = "AARSAK";
    public static final String TILTAK = "TILTAK";
    public static final String SVARMETODE = "SVARMETODE";
    public static final String SVAR_IKKE_NOEDVENDIG = "SVAR_IKKE_NOEDVENDIG";
    public static final String SVARMETODE_SPESIFISER = "SVARMETODE_SPESIFISER";
    public static final String FERDIG_DATO = "FERDIG_DATO";

    public static final Map<String, String> questionMap = new HashMap<>();

    static {
        questionMap.put(BEHANDLES_SOM_SERVICEKLAGE, "Skal klagen behandles etter serviceklagerutinen?");
        questionMap.put(BEHANDLES_SOM_SERVICEKLAGE_SPESIFISER, "Spesifiser");
        questionMap.put(FREMMET_DATO, "Angi dato bruker fremmet serviceklagen");
        questionMap.put(INNSENDER, "Hvem sendte inn serviceklagen?");
        questionMap.put(KANAL, "Angi kanal for serviceklagen");
        questionMap.put(PAAKLAGET_ENHET_ER_BEHANDLENDE, "Er klagen behandlet i enheten det klages på?");
        questionMap.put(ENHETSNUMMER_PAAKLAGET, "Angi enhetsnummer til enheten det klages på");
        questionMap.put(ENHETSNUMMER_BEHANDLENDE, "Angi enhetsnummer til enheten som behandler klagen");
        questionMap.put(GJELDER, "Hva mener du serviceklagen gjelder?");
        questionMap.put(YTELSE, "Hvilken ytelse mm. er mest relevant for serviceklagen?");
        questionMap.put(TEMA, "Hva gjelder serviceklagen?");
        questionMap.put(VENTE, "Vente på NAV");
        questionMap.put(TILGJENGELIGHET, "Tilgjengelighet");
        questionMap.put(INFORMASJON, "Informasjon");
        questionMap.put(VEILEDNING, "Veiledning og oppfølging mot arbeid");
        questionMap.put(TEMA_SPESIFISER, "Spesifiser");
        questionMap.put(UTFALL, "Hva er utfallet av serviceklagen?");
        questionMap.put(AARSAK, "Hva er, etter din mening, de bakenforliggende årsakene?");
        questionMap.put(TILTAK, "Hvordan kan NAV hindre at det samme skjer igjen, etter din mening?");
        questionMap.put(SVARMETODE, "Hvordan har bruker blitt svart?");
        questionMap.put(SVAR_IKKE_NOEDVENDIG, "Svar ikke nødvendig");
        questionMap.put(SVARMETODE_SPESIFISER, "Spesifiser");
        questionMap.put(FERDIG_DATO, "Angi dato serviceklagen var ferdigbehandlet og avsluttet i Gosys");
    }
}
