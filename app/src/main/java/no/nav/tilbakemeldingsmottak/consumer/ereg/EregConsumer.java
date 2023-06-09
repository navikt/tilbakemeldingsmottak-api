package no.nav.tilbakemeldingsmottak.consumer.ereg;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
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

import static java.lang.String.format;
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
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("Autentisering mot ereg feilet", e);
                throw new ClientErrorUnauthorizedException("Autentisering mot ereg feilet", e, ErrorCode.EREG_UNAUTHORIZED);
            }
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ClientErrorNotFoundException(format("Klientfeil ved kall mot ereg for organisasjonsnummer=%s (statuskode:%s). Body: %s", orgnr, e.getStatusCode(), e.getResponseBodyAsString()), e, ErrorCode.EREG_NOT_FOUND);
            }
            throw new ClientErrorException(format("Klientfeil ved kall mot ereg for organisasjonsnummer=%s (statuskode:%s). Body: %s", orgnr, e.getStatusCode(), e.getResponseBodyAsString()), e, ErrorCode.EREG_ERROR);
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException(format("Serverfeil ved kall mot ereg for organisasjonsnummer=%s (statuskode:%s). Body: %s", orgnr, e.getStatusCode(), e.getResponseBodyAsString()), e, ErrorCode.EREG_ERROR);
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
