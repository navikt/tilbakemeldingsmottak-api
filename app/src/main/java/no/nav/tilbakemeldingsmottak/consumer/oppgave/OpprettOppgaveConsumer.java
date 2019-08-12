package no.nav.tilbakemeldingsmottak.consumer.oppgave;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.exceptions.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.OpprettOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
import no.nav.tilbakemeldingsmottak.util.RestSecurityHeadersUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Component
public class OpprettOppgaveConsumer {
    private final RestTemplate restTemplate;
    private final String oppgaveUrl;
    private final RestSecurityHeadersUtils restSecurityHeadersUtils;

    public OpprettOppgaveConsumer(RestTemplateBuilder restTemplateBuilder,
                                  @Value("${oppgave_oppgaver_url}") String oppgaveUrl,
                                  ServiceuserAlias serviceuserAlias, RestSecurityHeadersUtils restSecurityHeadersUtils) {
        this.oppgaveUrl = oppgaveUrl;
        this.restSecurityHeadersUtils = restSecurityHeadersUtils;
        this.restTemplate = restTemplateBuilder
                .setReadTimeout(Duration.ofSeconds(20))
                .setConnectTimeout(Duration.ofSeconds(5))
                .basicAuthentication(serviceuserAlias.getUsername(), serviceuserAlias.getPassword()).build();
    }

    public String opprettOppgave(OpprettOppgaveRequestTo opprettOppgaveRequestTo) {
        if (log.isDebugEnabled()) {
            log.debug("Oppretter oppgave");
        }
        try {

            HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
            headers.set("X-Correlation-ID", MDC.get(MDC_CALL_ID));

            HttpEntity<OpprettOppgaveRequestTo> requestEntity = new HttpEntity<>(opprettOppgaveRequestTo, headers);

            ResponseEntity<String> response = restTemplate.exchange(oppgaveUrl, HttpMethod.POST, requestEntity, String.class);
            if (log.isDebugEnabled()) {
                log.debug("Oppgave opprettet");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OpprettJournalpostFunctionalException(String.format("opprettOppgave feilet funksjonelt med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        } catch (HttpServerErrorException e) {
            throw new OpprettOppgaveTechnicalException(String.format("opprettOppgave feilet teknisk med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        }
    }
}
