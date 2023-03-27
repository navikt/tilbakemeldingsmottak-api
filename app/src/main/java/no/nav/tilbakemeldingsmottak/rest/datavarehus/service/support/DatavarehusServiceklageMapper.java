package no.nav.tilbakemeldingsmottak.rest.datavarehus.service.support;

import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatavarehusServiceklageMapper {

    // Mapper fra Serviceklage (DAO) til DatavarehusServiceKlage (DTO) for å kunne separere hvilke felter som skal sendes til datavarehus
    public List<DatavarehusServiceklage> map(List<Serviceklage> serviceklageList){
        List<DatavarehusServiceklage> datavarehusServiceKlageList = new ArrayList<>();
        for (Serviceklage serviceklage : serviceklageList) {
            DatavarehusServiceklage datavarehusServiceklage = DatavarehusServiceklage.builder()
                    .journalpostId(serviceklage.getJournalpostId())
                    .opprettetDato(serviceklage.getOpprettetDato() != null ? serviceklage.getOpprettetDato().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .klagenGjelderId(serviceklage.getKlagenGjelderId())
                    .klagetyper(serviceklage.getKlagetyper())
                    .gjelderSosialhjelp(serviceklage.getGjelderSosialhjelp())
                    .klagetekst(serviceklage.getKlagetekst())
                    .behandlesSomServiceklage(serviceklage.getBehandlesSomServiceklage())
                    .behandlesSomServiceklageUtdypning(serviceklage.getBehandlesSomServiceklageUtdypning())
                    .fremmetDato(serviceklage.getFremmetDato() != null ? serviceklage.getFremmetDato().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                    .innsender(serviceklage.getInnsender())
                    .kanal(serviceklage.getKanal())
                    .kanalUtdypning(serviceklage.getKanalUtdypning())
                    .enhetsnummerPaaklaget(serviceklage.getEnhetsnummerPaaklaget())
                    .enhetsnummerBehandlende(serviceklage.getEnhetsnummerBehandlende())
                    .gjelder(serviceklage.getGjelder())
                    .beskrivelse(serviceklage.getBeskrivelse())
                    .ytelse(serviceklage.getYtelse())
                    .tema(serviceklage.getTema())
                    .temaUtdypning(serviceklage.getTemaUtdypning())
                    .veiledning(serviceklage.getVeiledning())
                    .utfall(serviceklage.getUtfall())
                    .aarsak(serviceklage.getAarsak())
                    .tiltak(serviceklage.getTiltak())
                    .svarmetode(serviceklage.getSvarmetode())
                    .svarmetodeUtdypning(serviceklage.getSvarmetodeUtdypning())
                    .avsluttetDato(serviceklage.getAvsluttetDato() != null ? serviceklage.getAvsluttetDato().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .skjemaVersjon(serviceklage.getSkjemaVersjon() != null ? serviceklage.getSkjemaVersjon().toString() : null)
                    .klassifiseringJson(serviceklage.getKlassifiseringJson())
                    .relatert(serviceklage.getRelatert())
                    .klagetypeUtdypning(serviceklage.getKlagetypeUtdypning())
                    .innlogget(serviceklage.getInnlogget())
                    .build();

            datavarehusServiceKlageList.add(datavarehusServiceklage);
        }
        return datavarehusServiceKlageList;
    }
}
