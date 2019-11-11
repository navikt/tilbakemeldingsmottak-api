package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static org.apache.cxf.common.util.CollectionUtils.isEmpty;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.SafJournalpostQueryService;
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentConsumer;
import no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument.HentDokumentResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat;
import no.nav.tilbakemeldingsmottak.exceptions.GyldigDokumentIkkeFunnetException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentDokumentResponse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class HentDokumentService {

    private SafJournalpostQueryService safJournalpostQueryService;
    private HentDokumentConsumer hentDokumentConsumer;


    @Inject
    public HentDokumentService(SafJournalpostQueryService safJournalpostQueryService,
                               HentDokumentConsumer hentDokumentConsumer) {
        this.safJournalpostQueryService = safJournalpostQueryService;
        this.hentDokumentConsumer = hentDokumentConsumer;
    }

    public HentDokumentResponse hentDokument(String journalpostId, String authorizationHeader) {
        Journalpost journalpost = safJournalpostQueryService.hentJournalpost(journalpostId, authorizationHeader);
        Variantformat variantformat;
        Journalpost.DokumentInfo dokumentInfo;
        if (isEmpty(journalpost.getDokumenter())) {
            return null;
        } else {
            dokumentInfo = journalpost.getDokumenter().get(0);
            if (dokumentInfo.getDokumentvarianter().stream().anyMatch(d -> d.getVariantformat().equals(Variantformat.SLADDET))) {
                variantformat = Variantformat.SLADDET;
            } else if (dokumentInfo.getDokumentvarianter().stream().anyMatch(d -> d.getVariantformat().equals(Variantformat.ARKIV))) {
                variantformat = Variantformat.ARKIV;
            } else {
                throw new GyldigDokumentIkkeFunnetException(String.format("Finner ikke gyldig dokument for journalpostId=%s", journalpostId));
            }
        }

        HentDokumentResponseTo safHentDokumentResponseTo = hentDokumentConsumer.hentDokument(journalpostId, dokumentInfo.getDokumentInfoId(), variantformat.name(), authorizationHeader);
        return HentDokumentResponse.builder()
                .dokument(safHentDokumentResponseTo.getDokument())
                .build();
    }
}
