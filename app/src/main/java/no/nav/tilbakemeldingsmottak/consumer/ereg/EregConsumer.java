package no.nav.tilbakemeldingsmottak.consumer.ereg;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorForbiddenException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorNotFoundException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@Component
@Slf4j
public class EregConsumer implements Ereg {

    private final String eregApiUrl;
    @Inject
    @Qualifier("basicclient")
    private RestTemplate restTemplate;

    public EregConsumer(@Value("${ereg.api.url}") String eregApiUrl) {
        this.eregApiUrl = eregApiUrl;
    }

    // FIXME: Bytt til WebClient
    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "eregHentInfo"}, percentiles = {0.5, 0.95}, histogram = true)
    public String hentInfo(String orgnr) {
        try {
            final String orgnrTrimmed = orgnr.trim();
            HttpHeaders headers = createHeaders();
            return restTemplate.exchange(eregApiUrl + "/v1/organisasjon/" + orgnrTrimmed,
                    HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            var statusCode = e.getStatusCode();
            var responseBody = e.getResponseBodyAsString();
            var errorMessage = String.format("Kall mot ereg feilet for orgnr: %s (statuskode: %s). Body: %s", orgnr, statusCode, responseBody);

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                log.error("Autentisering mot ereg feilet", e);
                throw new ClientErrorUnauthorizedException(errorMessage, e, ErrorCode.EREG_UNAUTHORIZED);
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                log.error("Mangler tilgang til å hente ut data fra ereg", e);
                throw new ClientErrorForbiddenException(errorMessage, e, ErrorCode.EREG_FORBIDDEN);
            }

            if (statusCode == HttpStatus.NOT_FOUND) {
                throw new ClientErrorNotFoundException(errorMessage, e, ErrorCode.EREG_NOT_FOUND);
            }

            if (statusCode.is4xxClientError()) {
                throw new ClientErrorException(errorMessage, e, ErrorCode.EREG_ERROR);
            }

            throw new ServerErrorException(errorMessage, e, ErrorCode.EREG_ERROR);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Nav-Consumer-Id", "Tilbakemeldingsmottak");
        headers.add("Nav-Call-Id", MDC.get(MDC_CALL_ID));
        return headers;
    }
}
