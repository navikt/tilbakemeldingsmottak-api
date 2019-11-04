package no.nav.tilbakemeldingsmottak.consumer.oppgave;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveTechnicalException;
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
public class OppgaveConsumer {
    private final RestTemplate restTemplate;
    private final String oppgaveUrl;
    private final RestSecurityHeadersUtils restSecurityHeadersUtils;

    public OppgaveConsumer(RestTemplateBuilder restTemplateBuilder,
                           @Value("${oppgave_oppgaver_url}") String oppgaveUrl,
                           ServiceuserAlias serviceuserAlias, RestSecurityHeadersUtils restSecurityHeadersUtils) {
        this.oppgaveUrl = oppgaveUrl;
        this.restSecurityHeadersUtils = restSecurityHeadersUtils;
        this.restTemplate = restTemplateBuilder
                .setReadTimeout(Duration.ofSeconds(20))
                .setConnectTimeout(Duration.ofSeconds(5))
                .basicAuthentication(serviceuserAlias.getUsername(), serviceuserAlias.getPassword()).build();
    }

    public OpprettOppgaveResponseTo opprettOppgave(OpprettOppgaveRequestTo opprettOppgaveRequestTo) {
        if (log.isDebugEnabled()) {
            log.debug("Oppretter oppgave");
        }
        try {

            HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
            headers.set("X-Correlation-ID", MDC.get(MDC_CALL_ID));

            HttpEntity<OpprettOppgaveRequestTo> requestEntity = new HttpEntity<>(opprettOppgaveRequestTo, headers);

            ResponseEntity<OpprettOppgaveResponseTo> response = restTemplate.exchange(oppgaveUrl, HttpMethod.POST, requestEntity, OpprettOppgaveResponseTo.class);
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

    public String endreOppgave(EndreOppgaveRequestTo endreOppgaveRequestTo) {
        if (log.isDebugEnabled()) {
            log.debug("Lukker oppgave");
        }
        try {
            HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
            headers.set("X-Correlation-ID", MDC.get(MDC_CALL_ID));

            HttpEntity<EndreOppgaveRequestTo> requestEntity = new HttpEntity<>(endreOppgaveRequestTo, headers);

            ResponseEntity<String> response = restTemplate.exchange(oppgaveUrl+"/"+endreOppgaveRequestTo.getId(), HttpMethod.PUT, requestEntity, String.class);
            if (log.isDebugEnabled()) {
                log.debug("Oppgave lukket");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OpprettJournalpostFunctionalException(String.format("endreOppgave feilet funksjonelt med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        } catch (HttpServerErrorException e) {
            throw new OpprettOppgaveTechnicalException(String.format("endreOppgave feilet teknisk med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        }
    }

    public HentOppgaveResponseTo hentOppgave(String oppgaveId) {
        if (log.isDebugEnabled()) {
            log.debug("Henter oppgave med id={}", oppgaveId);
        }
        try {
            HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
            headers.set("X-Correlation-ID", MDC.get(MDC_CALL_ID));

            HttpEntity<EndreOppgaveRequestTo> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<HentOppgaveResponseTo> response = restTemplate.exchange(oppgaveUrl+"/"+oppgaveId, HttpMethod.GET, requestEntity, HentOppgaveResponseTo.class);
            if (log.isDebugEnabled()) {
                log.debug("Oppgave hentet");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OpprettJournalpostFunctionalException(String.format("hentOppgave feilet funksjonelt med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        } catch (HttpServerErrorException e) {
            throw new OpprettOppgaveTechnicalException(String.format("hentOppgave feilet teknisk med statusKode=%s. Feilmelding=%s", e
                    .getStatusCode(), e.getMessage()), e);
        }
    }
}
