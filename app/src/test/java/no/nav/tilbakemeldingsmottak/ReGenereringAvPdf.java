package no.nav.tilbakemeldingsmottak;

import no.nav.tilbakemeldingsmottak.model.Innmelder;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.GjelderSosialhjelpEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAvEnum;
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReGenereringAvPdf {


    private OpprettServiceklageRequest createOpprettServiceklageRequestPrivatperson(List<String> klage) {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.PRIVATPERSON)
                .innmelder(Innmelder.builder()
                        .navn(klage.get(32))
                        .telefonnummer(klage.get(33))
                        .personnummer(klage.get(3))
                        .build())
                .klagetyper(konverterTilKlageType(Arrays.stream(klage.get(4).split(",")).toList()))
                .gjelderSosialhjelp(findSosialhjelpTypeText(klage.get(5)))
                .klagetekst(klage.get(6))
                .oenskerAaKontaktes(klage.get(25) != null)
                .enhetsnummerPaaklaget(klage.get(13))
                .klagetypeUtdypning(klage.get(30))
                .build();
    }

    private LocalDateTime mapTilLocalDateTime(String fremmetText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss");
        return LocalDateTime.parse(fremmetText.substring(0, 19), formatter);
    }

    private List<KlagetyperEnum> konverterTilKlageType(List<String> klageTypeStrenger) {
        List<KlagetyperEnum> typer = new LinkedList<>();
        for (String klageStreng : klageTypeStrenger) {
            typer.add(findByText(klageStreng.trim()));
        }
        return typer;
    }

    private KlagetyperEnum findByText(String text) {
        for (KlagetyperEnum v : KlagetyperEnum.values()) {
            if (v.value.equalsIgnoreCase(text)) {
                return v;
            }
        }
        return null;
    }

    private GjelderSosialhjelpEnum findSosialhjelpTypeText(String text) {
        for (GjelderSosialhjelpEnum t : GjelderSosialhjelpEnum.values()) {
            if (t.value.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return GjelderSosialhjelpEnum.VET_IKKE;
    }

    private OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvPerson(List<String> klage) {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.ANNEN_PERSON)
                .innmelder(Innmelder.builder()
                        .navn(klage.get(32))
                        .telefonnummer(klage.get(33))
                        .harFullmakt(true)
                        .personnummer(klage.get(3))
                        .rolle("Fullmektig")
                        .build())
                .paaVegneAvPerson(PaaVegneAvPerson.builder()
                        .navn(klage.get(34))
                        .personnummer(klage.get(35))
                        .build())
                .klagetyper(konverterTilKlageType(Arrays.stream(klage.get(4).split(",")).toList()))
                .klagetekst(klage.get(6))
                .oenskerAaKontaktes(klage.get(25) != null)
                .build();
    }

    @Test
    public void generatePdfs() {
        try {
            Map<Integer, List<String>> serviceklager = new ReadXslxFile().readExcelFile("serviceklage-eks.xlsx");
            PdfService pdfService = new PdfService();

            boolean header = true;
            for (List<String> klage : serviceklager.values()) {
                if (header) {
                    header = false;
                    continue;
                }
                if ("Bruker selv som privatperson".equalsIgnoreCase(klage.get(10))) {
                    OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson(klage);
                    byte[] klagePdf = pdfService.opprettServiceklagePdf(request, "1".equals(klage.get(31)), mapTilLocalDateTime(klage.get(2)));
                    //writeBytesToFile(klagePdf, "src/test/resources/serviceklage-jp-"+ klage.get(1) +".pdf");
                    assertTrue(klagePdf != null && klagePdf.length > 0);
                } else if ("På vegne av en annen privatperson".equalsIgnoreCase(klage.get(10))) {
                    OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson(klage);
                    byte[] klagePdf = pdfService.opprettServiceklagePdf(request, "1".equals(klage.get(31)), mapTilLocalDateTime(klage.get(2)));
                    //writeBytesToFile(klagePdf, "src/test/resources/serviceklage-jp-"+ klage.get(1) +".pdf");
                    assertTrue(klagePdf != null && klagePdf.length > 0);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lesing fra excel fil feilet" + e.getMessage());
        }
    }


    private void writeBytesToFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }


}
