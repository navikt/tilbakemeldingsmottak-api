package no.nav.tilbakemeldingsmottak.consumer.norg2;

import jakarta.inject.Inject;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static no.nav.tilbakemeldingsmottak.config.cache.CacheConfig.NORG2_CACHE;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;


@Component
public class Norg2Consumer {

    private final String norg2Url;
    @Inject
    @Qualifier("basicclient")
    private RestTemplate restTemplate;

    public Norg2Consumer(@Value("${norg2.api.v1.url}") String norg2Url) {
        this.norg2Url = norg2Url;
    }

    // FIXME: Bytt til WebClient
    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentEnheter"}, percentiles = {0.5, 0.95}, histogram = true)
    @Retryable(include = ServerErrorException.class, backoff = @Backoff(delay = 1000))
    @Cacheable(NORG2_CACHE)
    public List<Enhet> hentEnheter() {
        try {
            return restTemplate.exchange(norg2Url + "/enhet",
                    HttpMethod.GET, new HttpEntity<>(createHeaders()), new ParameterizedTypeReference<List<Enhet>>() {
                    }).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403 || e.getStatusCode().value() == 401) {
                throw new ClientErrorUnauthorizedException("Autentisering mot norg2 feilet", e, ErrorCode.NORG2_UNAUTHORIZED);
            }
            throw new ClientErrorException(String.format("Klientfeil ved kall mot norg2 for å hente enheter (statusCode:%s)", e.getStatusCode()), e, ErrorCode.NORG2_ERROR);
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException(String.format("Serverfeil ved kall mot norg2 for å hente enheter (statusCode:%s)", e.getStatusCode()), e, ErrorCode.NORG2_ERROR);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Nav-Consumer-Id", "tilbakemeldingsmottak");
        headers.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID));
        return headers;
    }
}
