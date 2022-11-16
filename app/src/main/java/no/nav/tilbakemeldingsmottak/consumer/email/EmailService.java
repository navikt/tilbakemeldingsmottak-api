package no.nav.tilbakemeldingsmottak.consumer.email;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.generated.BodyType;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, BodyType contentType, String content) throws SendEmailException;

    void sendSimpleMessage(String mottaker, String subject, BodyType contentType, String content) throws SendEmailException;
}