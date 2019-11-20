package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.SUBJECT_JOURNALPOST_FEILET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.SUBJECT_KOMMUNAL_KLAGE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.SUBJECT_OPPGAVE_FEILET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.TEXT_JOURNALPOST_FEILET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.TEXT_KOMMUNAL_KLAGE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.MailConstants.TEXT_OPPGAVE_FEILET;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageMailException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.GjelderSosialhjelpType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettServiceklageRequestMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpprettServiceklageService {

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    private final ServiceklageRepository serviceklageRepository;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;
    private final OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper;
    private final OpprettJournalpostConsumer opprettJournalpostConsumer;
    private final OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private final OppgaveConsumer oppgaveConsumer;
    private final PdfService pdfService;
    private final AbstractEmailService emailService;

    public OpprettServiceklageResponse opprettServiceklage(OpprettServiceklageRequest request) throws DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        byte[] fysiskDokument = pdfService.opprettPdf(request);

        if (isKommunalKlage(request)) {
            sendEmail(SUBJECT_KOMMUNAL_KLAGE,
                    TEXT_KOMMUNAL_KLAGE,
                    fysiskDokument);
            return OpprettServiceklageResponse.builder()
                    .message("Klagen er en kommunal klage, videresendt på mail til " + emailToAddress)
                    .build();
        }

        OpprettJournalpostResponseTo opprettJournalpostResponseTo = forsoekOpprettJournalpost(request, fysiskDokument);
        log.info("Journalpost med journalpostId={} opprettet", opprettJournalpostResponseTo.getJournalpostId());

        serviceklage.setJournalpostId(opprettJournalpostResponseTo.getJournalpostId());
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());

        OpprettOppgaveResponseTo opprettOppgaveResponseTo = forsoekOpprettOppgave(serviceklage.getKlagenGjelderId(), request.getPaaVegneAv(), opprettJournalpostResponseTo);
        log.info("Oppgave med oppgaveId={} opprettet", opprettOppgaveResponseTo.getId());

        return OpprettServiceklageResponse.builder()
                .message("Serviceklage opprettet")
                .serviceklageId(serviceklage.getServiceklageId().toString())
                .journalpostId(serviceklage.getJournalpostId())
                .oppgaveId(opprettOppgaveResponseTo.getId())
                .build();
    }

    private boolean isKommunalKlage(OpprettServiceklageRequest request) {
        return request.getKlagetyper().contains(Klagetype.LOKALT_NAV_KONTOR) &&
                GjelderSosialhjelpType.JA.equals(request.getGjelderSosialhjelp());
    }

    private OpprettJournalpostResponseTo forsoekOpprettJournalpost(OpprettServiceklageRequest request, byte[] fysiskDokument) {
        try {
            OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
            return opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo);
        } catch (OpprettJournalpostFunctionalException | OpprettJournalpostTechnicalException e) {
            sendEmail(SUBJECT_JOURNALPOST_FEILET,
                    TEXT_JOURNALPOST_FEILET,
                    fysiskDokument);
            throw new EksterntKallException("Feil ved opprettelse av journalpost, klage videresendt til " + emailToAddress);
        }
    }

    private OpprettOppgaveResponseTo forsoekOpprettOppgave(String id, PaaVegneAvType paaVegneAvType, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        try {
            OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(id, paaVegneAvType, opprettJournalpostResponseTo);
            return oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);
        } catch (OpprettOppgaveFunctionalException | OpprettOppgaveTechnicalException e) {
            sendEmail(SUBJECT_OPPGAVE_FEILET,
                    TEXT_OPPGAVE_FEILET + opprettJournalpostResponseTo.getJournalpostId());
            throw new EksterntKallException("Feil ved opprettelse av oppgave, journalpostId videresendt til " + emailToAddress);
        }
    }

    private void sendEmail(String subject, String text, byte[] fysiskDokument) {
        try {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(text);

            MimeMultipart content = new MimeMultipart();
            content.addBodyPart(textBodyPart);

            if (fysiskDokument != null) {
                DataSource dataSource = new ByteArrayDataSource(fysiskDokument, "application/pdf");
                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                pdfBodyPart.setFileName("klage.pdf");
                content.addBodyPart(pdfBodyPart);
            }

            MimeMessage message = emailService.getEmailSender().createMimeMessage();
            message.setHeader("Content-Encoding", "UTF-8");
            message.setSender(new InternetAddress(emailFromAddress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailToAddress));
            message.setSubject(subject);
            message.setContent(content);

            emailService.sendMail(message);
        } catch (MessagingException e) {
            throw new ServiceklageMailException("Kan ikke sende mail");
        }
    }

    private void sendEmail(String subject, String text) {
        sendEmail(subject, text, null);
    }
}