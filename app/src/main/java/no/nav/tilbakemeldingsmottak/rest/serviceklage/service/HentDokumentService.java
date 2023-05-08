package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.SafJournalpostQueryService;
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentConsumer;
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat;
import no.nav.tilbakemeldingsmottak.exceptions.JournalpostManglerDokumentException;
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.stereotype.Service;

import static org.apache.cxf.common.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class HentDokumentService {

    private final SafJournalpostQueryService safJournalpostQueryService;
    private final HentDokumentConsumer hentDokumentConsumer;
    private final OidcUtils oidcUtils;
    private final PdfService pdfService;

    public HentDokumentResponse hentDokument(String journalpostId) {
        String authorizationHeader = "Bearer " + oidcUtils.getFirstValidToken();
        Journalpost journalpost = safJournalpostQueryService.hentJournalpost(journalpostId, authorizationHeader);
        Variantformat variantformat;
        Journalpost.DokumentInfo dokumentInfo;
        if (isEmpty(journalpost.getDokumenter())) {
            throw new JournalpostManglerDokumentException(String.format("Fant ingen dokument på journalpost %s", journalpostId));
        } else {
            dokumentInfo = journalpost.getDokumenter().get(0);
            if (dokumentInfo.getDokumentvarianter().stream().anyMatch(d -> d.getVariantformat().equals(Variantformat.SLADDET))) {
                variantformat = Variantformat.SLADDET;
            } else if (dokumentInfo.getDokumentvarianter().stream().anyMatch(d -> d.getVariantformat().equals(Variantformat.ARKIV))) {
                variantformat = Variantformat.ARKIV;
            } else {
                throw new JournalpostManglerDokumentException(String.format("Fant ingen tilgjengelig dokument på journalpost %s", journalpostId));
            }
        }

        HentDokumentResponseTo safHentDokumentResponseTo = hentDokumentConsumer.hentDokument(journalpostId, dokumentInfo.getDokumentInfoId(), variantformat.name(), authorizationHeader);
        return HentDokumentResponse.builder()
                .dokument(safHentDokumentResponseTo.getDokument())
                .build();
    }

}
