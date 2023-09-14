package no.nav.tilbakemeldingsmottak.rest.common.pdf;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.generer.PdfGeneratorService;
import no.nav.tilbakemeldingsmottak.generer.modeller.ServiceklagePdfModell;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.Klagetyper;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public final class PdfService {

    public byte[] opprettServiceklagePdf(OpprettServiceklageRequest request, boolean innlogget) {
        return opprettServiceklagePdf(request, innlogget, LocalDateTime.now());
    }

    public byte[] opprettServiceklagePdf(OpprettServiceklageRequest request, boolean innlogget, LocalDateTime fremmet) {
        try {
            var serviceklagePdfModell = new ServiceklagePdfModell(KANAL_SERVICEKLAGESKJEMA_ANSWER, !innlogget ? "OBS! Klagen er sendt inn uinnlogget" : null, lagKlageMap(request, fremmet));
            return new PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell);
        } catch (Exception e) {
            throw new ServerErrorException("Opprett serviceklage PDF", e);
        }
    }

    private Map<String, String> lagKlageMap(OpprettServiceklageRequest request, LocalDateTime fremmet) {
        Map<String, String> klageMap = new HashMap<>();

        klageMap.put("Kanal", KANAL_SERVICEKLAGESKJEMA_ANSWER);
        klageMap.put("Dato fremmet", fremmet.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        if (!isBlank(request.getInnmelder().getNavn())) {
            klageMap.put("Navn til innmelder", request.getInnmelder().getNavn());
        }

        if (!isBlank(request.getInnmelder().getPersonnummer())) {
            klageMap.put("Personnummer til innmelder", request.getInnmelder().getPersonnummer());
        }

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON -> {
            }
            case ANNEN_PERSON -> {
                if (!isBlank(request.getInnmelder().getRolle())) {
                    klageMap.put("Innmelders rolle", request.getInnmelder().getRolle());
                    klageMap.put("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei");
                }

                klageMap.put("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn());
                klageMap.put("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer());
            }
            case BEDRIFT -> {
                if (!isBlank(request.getInnmelder().getRolle())) {
                    klageMap.put("Innmelders rolle", request.getInnmelder().getRolle());
                    klageMap.put("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei");
                }
                
                klageMap.put("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn());
                klageMap.put("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer());
            }
        }

        if (!isBlank(request.getEnhetsnummerPaaklaget())) {
            klageMap.put("Påklaget enhet", request.getEnhetsnummerPaaklaget());
        }
        klageMap.put("Klagetype", StringUtils.join(request.getKlagetyper().stream().map(x -> x.getValue()).collect(Collectors.toList()), ", "));
        if (request.getKlagetyper().contains(Klagetyper.LOKALT_NAV_KONTOR)) {
            klageMap.put("Gjelder økonomisk sosialhjelp/sosiale tjenester", request.getGjelderSosialhjelp().getValue());
        }
        if (request.getKlagetyper().contains(Klagetyper.ANNET) && !isBlank(request.getKlagetypeUtdypning())) {
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
            var serviceklagePdfModell = new ServiceklagePdfModell("Serviceklage klassifisering", null, questionAnswerMap);
            return new PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell);
        } catch (Exception e) {
            throw new ServerErrorException("Opprett serviceklage klassifiserings PDF", e);
        }

    }

}