package no.nav.tilbakemeldingsmottak.consumer.email;

import com.microsoft.graph.models.BodyType;

public interface EmailService {

    void sendSimpleMessage(String mottaker, String subject, String content) throws SendEmailException;
}