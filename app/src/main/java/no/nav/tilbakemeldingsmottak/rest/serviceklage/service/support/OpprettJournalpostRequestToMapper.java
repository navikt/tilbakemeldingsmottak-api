package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.*;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpprettJournalpostRequestToMapper {

    private static final String TEMA_SER = "SER";
    private static final String KANAL_NAV_NO = "NAV_NO";
    private static final String KANAL_NAV_NO_UINNLOGGET = "NAV_NO_UINNLOGGET";
    private static final String TITTEL_SERVICEKLAGE = "Serviceklage";
    private static final String JOURNALFOERENDE_ENHET = "9999";
    private static final String FILTYPE_PDFA = "PDFA";
    private static final String VARIANTFORMAT_ARKIV = "ARKIV";

    public OpprettJournalpostRequestTo map(OpprettServiceklageRequest request, byte[] fysiskDokument, boolean innlogget) {
        OpprettJournalpostRequestTo opprettJournalpostRequestTo = OpprettJournalpostRequestTo.builder()
                .avsenderMottaker(AvsenderMottaker.builder()
                        .id(request.getInnmelder().getPersonnummer())
                        .idType(request.getInnmelder().getPersonnummer() != null ? AvsenderMottakerIdType.FNR : null)
                        .navn(request.getInnmelder().getNavn())
                        .build())
                .sak(Sak.builder()
                        .sakstype(Sakstype.GENERELL_SAK)
                        .build())
                .bruker(mapBruker(request))
                .journalpostType(JournalpostType.INNGAAENDE)
                .journalfoerendeEnhet(JOURNALFOERENDE_ENHET)

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