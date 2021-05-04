package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class FeilOgManglerIT extends AbstractIT {

    private static final String URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler";


}
