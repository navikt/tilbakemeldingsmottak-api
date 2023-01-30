package no.nav.tilbakemeldingsmottak.rest.common.pdf;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.PdfException;
import no.nav.tilbakemeldingsmottak.generer.PdfGenerator;
import no.nav.tilbakemeldingsmottak.serviceklage.Klagetype;
import no.nav.tilbakemeldingsmottak.serviceklage.OpprettServiceklageRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class PdfService {

    public byte[] opprettServiceklagePdf(OpprettServiceklageRequest request, boolean innlogget)  {
        try {
            return new PdfGenerator().genererPdf(
                    KANAL_SERVICEKLAGESKJEMA_ANSWER,
                    !innlogget ? "OBS! Klagen er sendt inn uinnlogget" : null,
                    lagKlageMap(request));
        } catch (Exception e) {
            throw new PdfException("Opprett serviceklage PDF", e );
        }
    }

    private Map<String, String> lagKlageMap(OpprettServiceklageRequest request) {
        Map<String, String> klageMap = new HashMap<>();

        klageMap.put("Kanal", KANAL_SERVICEKLAGESKJEMA_ANSWER);
        klageMap.put("Dato fremmet", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        if (!isBlank(request.getInnmelder().getNavn())) {
            klageMap.put("Navn til innmelder", request.getInnmelder().getNavn());
        }

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                klageMap.put("Personnummer til innmelder", request.getInnmelder().getPersonnummer());
                break;
            case ANNEN_PERSON:
                klageMap.put("Innmelders rolle", request.getInnmelder().getRolle());
                klageMap.put("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei");
                klageMap.put("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn());
                klageMap.put("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer());
                break;
            case BEDRIFT:
                if (!isBlank(request.getInnmelder().getRolle())) {
                    klageMap.put("Innmelders rolle", request.getInnmelder().getRolle());
                }
                klageMap.put("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn());
                klageMap.put("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer());
        }

        if (!isBlank(request.getEnhetsnummerPaaklaget())) {
            klageMap.put("Påklaget enhet", request.getEnhetsnummerPaaklaget());
        }
        klageMap.put("Klagetype", StringUtils.join(request.getKlagetyper().stream().map(k -> k.text).collect(Collectors.toList()), ", "));
        if (request.getKlagetyper().contains(Klagetype.LOKALT_NAV_KONTOR)) {
            klageMap.put("Gjelder økonomisk sosialhjelp/sosiale tjenester", request.getGjelderSosialhjelp().text);
        }
        if (request.getKlagetyper().contains(Klagetype.ANNET) && !isBlank(request.getKlagetypeUtdypning())) {
            klageMap.put("Klagetype spesifisert i fritekst", request.getKlagetypeUtdypning());

        }
        klageMap.put("Klagetekst", request.getKlagetekst());
        if (request.getOenskerAaKontaktes() != null) {
            klageMap.put("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei");
        }
        if (!isBlank(request.getInnmelder().getTelefonnummer())) {
            klageMap.put("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer());
        }

        return klageMap;
    }

    public byte[] opprettKlassifiseringPdf(Map<String, String> questionAnswerMap) {

        try {
            return new PdfGenerator().genererPdf(
                    "Serviceklage klassifisering",
                    null,
                    questionAnswerMap);
        } catch (Exception e) {
            throw new PdfException("Opprett serviceklage klassifiserings PDF", e );
        }

    }

}
