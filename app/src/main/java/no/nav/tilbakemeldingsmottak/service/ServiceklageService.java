package no.nav.tilbakemeldingsmottak.service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileNotFoundException;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;

    Font regular = new Font(Font.FontFamily.HELVETICA, 12);
    Font bold = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository, OpprettServiceklageRequestMapper opprettServiceklageRequestMapper) {
        this.serviceklageRepository = serviceklageRepository;
        this.opprettServiceklageRequestMapper = opprettServiceklageRequestMapper;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) throws FileNotFoundException , DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());
        Document pdf = opprettPdf(request);

        return serviceklage.getServiceklageId();
    }

    private Document opprettPdf(OpprettServiceklageRequest request) throws FileNotFoundException , DocumentException {
        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("serviceklage.pdf"));

        document.open();

        document.add(createParagraph("Navn til innmelder", request.getInnmelder().getNavn()));
        document.add(createParagraph("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer()));

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                document.add(createParagraph("Personnummer til innmelder", request.getInnmelder().getPersonnummer()));
                break;
            case ANNEN_PERSON:
                document.add(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                document.add(createParagraph("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei"));
                document.add(createParagraph("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn()));
                document.add(createParagraph("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer()));
                break;
            case BEDRIFT:
                document.add(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                document.add(createParagraph("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn()));
                document.add(createParagraph("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer()));
                document.add(createParagraph("Adresse til forulempet bedrift", request.getPaaVegneAvBedrift().getPostadresse()));
                document.add(createParagraph("Telefonnummer til forulempet bedrift", request.getPaaVegneAvBedrift().getTelefonnummer()));
        }

        document.add(createParagraph("Klagetype", request.getKlagetype()));
        document.add(createParagraph("Klagetekst", request.getKlagetekst()));
        document.add(createParagraph("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei"));

        document.close();

        return document;
    }

    private Paragraph createParagraph(String fieldname, String content) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(fieldname + ": ", bold));
        p.add(new Chunk(content, regular));
        return p;
    }
}
