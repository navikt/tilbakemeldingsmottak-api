package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.AvsenderMottaker;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.AvsenderMottakerIdType;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.Bruker;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.BrukerIdType;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.Dokument;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.DokumentVariant;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.JournalpostType;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.OpprettJournalpostRequestTo;
import org.springframework.stereotype.Component;

@Component
public class OpprettJournalpostRequestToMapper {

    private final static String TEMA_SER = "SER";
    private final static String KANAL_NAV_NO_UINNLOGGET = "NAV_NO_UINNLOGGET";
    private final static String TITTEL_SERVICEKLAGE = "Serviceklage";
    private final static String JOURNALFOERENDE_ENHET = "9999";
    private final static String FILTYPE_PDFA = "PDFA";
    private final static String VARIANTFORMAT_ARKIV = "ARKIV";

    public OpprettJournalpostRequestTo map(OpprettServiceklageRequest request, byte[] fysiskDokument) {
        OpprettJournalpostRequestTo opprettJournalpostRequestTo = OpprettJournalpostRequestTo.builder()
                .avsenderMottaker( AvsenderMottaker.builder()
                        .id(request.getInnmelder().getPersonnummer())
                        .idType(AvsenderMottakerIdType.FNR)
                        .navn(request.getInnmelder().getNavn())
                        .build())
                .bruker(mapBruker(request))
                .journalpostType(JournalpostType.INNGAAENDE)
                .journalfoerendeEnhet(JOURNALFOERENDE_ENHET)
                .tema(TEMA_SER)
                .tittel(TITTEL_SERVICEKLAGE)
                .kanal(KANAL_NAV_NO_UINNLOGGET)
                .build();

        opprettJournalpostRequestTo.getDokumenter().add(buildDokument(fysiskDokument));

        return opprettJournalpostRequestTo;
    }

    private Dokument buildDokument(byte[] fysiskDokument) {
        Dokument dokument = Dokument.builder()
                .tittel(TITTEL_SERVICEKLAGE)
                .build();

        DokumentVariant dokumentVariant = DokumentVariant.builder()
                .filtype(FILTYPE_PDFA)
                .fysiskDokument(fysiskDokument)
                .variantformat(VARIANTFORMAT_ARKIV)
                .build();

        dokument.getDokumentvarianter().add(dokumentVariant);
        return dokument;
    }

    private Bruker mapBruker(OpprettServiceklageRequest request) {
        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                return Bruker.builder()
                        .id(request.getInnmelder().getPersonnummer())
                        .idType(BrukerIdType.FNR)
                        .build();
            case ANNEN_PERSON:
                return Bruker.builder()
                        .id(request.getPaaVegneAvPerson().getPersonnummer())
                        .idType(BrukerIdType.FNR)
                        .build();
            case BEDRIFT:
                return Bruker.builder()
                        .id(request.getPaaVegneAvBedrift().getOrganisasjonsnummer())
                        .idType(BrukerIdType.ORGNR)
                        .build();
            default:
                return null;
        }
    }

}