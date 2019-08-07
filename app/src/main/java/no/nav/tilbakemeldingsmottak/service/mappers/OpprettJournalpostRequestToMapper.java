package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.AvsenderMottaker;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.AvsenderMottakerIdType;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.Bruker;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.BrukerIdType;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.Dokument;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.DokumentVariant;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.JournalpostType;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import org.springframework.stereotype.Component;

@Component
public class OpprettJournalpostRequestToMapper {

    private static final String TEMA_SER = "SER";
    private static final String KANAL_NAV_NO_UINNLOGGET = "NAV_NO_UINNLOGGET";
    private static final String TITTEL_SERVICEKLAGE = "Serviceklage";
    private static final String JOURNALFOERENDE_ENHET = "9999";
    private static final String FILTYPE_PDFA = "PDFA";
    private static final String VARIANTFORMAT_ARKIV = "ARKIV";

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
        Bruker bruker = null;
        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                bruker = Bruker.builder()
                        .id(request.getInnmelder().getPersonnummer())
                        .idType(BrukerIdType.FNR)
                        .build();
                break;
            case ANNEN_PERSON:
                bruker = Bruker.builder()
                        .id(request.getPaaVegneAvPerson().getPersonnummer())
                        .idType(BrukerIdType.FNR)
                        .build();
                break;
            case BEDRIFT:
                bruker = Bruker.builder()
                        .id(request.getPaaVegneAvBedrift().getOrganisasjonsnummer())
                        .idType(BrukerIdType.ORGNR)
                        .build();
                break;
        }
        return bruker;
    }

}