package no.nav.tilbakemeldingsmottak.rest.datavarehus.service.support;

import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatavarehusServiceklageMapper {

    // Mapper fra Serviceklage (DAO) til DatavarehusServiceKlage (DTO) for å kunne separere hvilke felter som skal sendes til datavarehus
    public List<DatavarehusServiceklage> map(List<Serviceklage> serviceklageList) {
        List<DatavarehusServiceklage> datavarehusServiceKlageList = new ArrayList<>();
        for (Serviceklage serviceklage : serviceklageList) {
            DatavarehusServiceklage datavarehusServiceklage = new DatavarehusServiceklage(
                    serviceklage.getJournalpostId(),
                    serviceklage.getOpprettetDato() != null ? serviceklage.getOpprettetDato().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null,
                    serviceklage.getKlagenGjelderId(),
                    serviceklage.getKlagetyper(),
                    serviceklage.getGjelderSosialhjelp(),
                    serviceklage.getKlagetekst(),
                    serviceklage.getBehandlesSomServiceklage(),
                    serviceklage.getBehandlesSomServiceklageUtdypning(),
                    serviceklage.getFremmetDato() != null ? serviceklage.getFremmetDato().format(DateTimeFormatter.ISO_LOCAL_DATE) : null,
                    serviceklage.getInnsender(),
                    serviceklage.getKanal(),
                    serviceklage.getKanalUtdypning(),
                    serviceklage.getEnhetsnummerPaaklaget(),
                    serviceklage.getEnhetsnummerBehandlende(),
                    serviceklage.getGjelder(),
                    serviceklage.getBeskrivelse(),
                    serviceklage.getYtelse(),
                    serviceklage.getTema(),
                    serviceklage.getTemaUtdypning(),
                    serviceklage.getVeiledning(),
                    serviceklage.getUtfall(),
                    serviceklage.getAarsak(),
                    serviceklage.getTiltak(),
                    serviceklage.getSvarmetode(),
                    serviceklage.getSvarmetodeUtdypning(),
                    serviceklage.getAvsluttetDato() != null ? serviceklage.getAvsluttetDato().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null,
                    serviceklage.getSkjemaVersjon() != null ? serviceklage.getSkjemaVersjon().toString() : null,
                    serviceklage.getKlassifiseringJson(),
                    serviceklage.getRelatert(),
                    serviceklage.getKlagetypeUtdypning(),
                    serviceklage.getInnlogget()
            );
            
            datavarehusServiceKlageList.add(datavarehusServiceklage);
        }
        return datavarehusServiceKlageList;
    }
}
