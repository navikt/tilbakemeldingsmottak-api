package no.nav.tilbakemeldingsmottak.consumer.email;

import java.util.List;

public interface EmailService {

    void sendSimpleMessage(String mottaker, String subject, String content) throws SendEmailException;
    void sendSimpleMessage(List<String> mottakere, String subject, String content) throws SendEmailException;

    void sendMessageWithAttachments(String mottaker, String subject, String content, byte[] attachment, String attachmentName) throws SendEmailException;
    void sendMessageWithAttachments(List<String> mottakere, String subject, String content, byte[] attachment, String attachmentName) throws SendEmailException;
}