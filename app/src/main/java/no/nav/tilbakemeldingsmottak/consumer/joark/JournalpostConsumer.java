package no.nav.tilbakemeldingsmottak.consumer.joark;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostTechnicalException;
import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
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
public class JournalpostConsumer {

	private final RestTemplate restTemplate;
	private final String journalpostUrl;
	private final RestSecurityHeadersUtils restSecurityHeadersUtils;

	private static final String FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true";

	public JournalpostConsumer(RestTemplateBuilder restTemplateBuilder,
							   @Value("${Journalpost_v1_url}") String journalpostUrl,
							   ServiceuserAlias serviceuserAlias, RestSecurityHeadersUtils restSecurityHeadersUtils) {
		this.journalpostUrl = journalpostUrl;
		this.restSecurityHeadersUtils = restSecurityHeadersUtils;
		this.restTemplate = restTemplateBuilder
				.setReadTimeout(Duration.ofSeconds(20))
				.setConnectTimeout(Duration.ofSeconds(5))
				.basicAuthentication(serviceuserAlias.getUsername(), serviceuserAlias.getPassword()).build();
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "opprettJournalpost"}, percentiles = {0.5, 0.95}, histogram = true)
	public OpprettJournalpostResponseTo opprettJournalpost(OpprettJournalpostRequestTo opprettJournalpostRequestTo) {
		if (log.isDebugEnabled()) {
			log.debug("Oppretter journalpost");
		}
		try {

			HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
			headers.set("Nav-Callid", MDC.get(MDC_CALL_ID));
			headers.set("Nav-Consumer-Id", "srvtilbakemeldings");

			HttpEntity<OpprettJournalpostRequestTo> requestEntity = new HttpEntity<>(opprettJournalpostRequestTo, headers);

			String opprettJournalpostUrl = journalpostUrl + "/journalpost/";

			ResponseEntity<OpprettJournalpostResponseTo> response = restTemplate.exchange(opprettJournalpostUrl + FORSOEK_FERDIGSTILL, HttpMethod.POST, requestEntity, OpprettJournalpostResponseTo.class);
			if (log.isDebugEnabled()) {
				log.debug("Journalpost med journalpostId={} opprettet", response.getBody().getJournalpostId());
			}
			return response.getBody();
		} catch (HttpClientErrorException e) {
			throw new OpprettJournalpostFunctionalException(String.format("opprettJournalpost feilet funksjonelt med statusKode=%s. Feilmelding=%s", e
					.getStatusCode(), e.getMessage()), e);
		} catch (HttpServerErrorException e) {
			throw new OpprettJournalpostTechnicalException(String.format("opprettJournalpost feilet teknisk med statusKode=%s. Feilmelding=%s", e
					.getStatusCode(), e.getMessage()), e);
		}
	}
}