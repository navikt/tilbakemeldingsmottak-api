package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil.createAuthHeaderFromToken;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@Slf4j
@Component
public class HentDokumentConsumer implements HentDokument {

    private final String hentDokumentUrl;
    @Inject
    @Qualifier("safclient")
    private WebClient webClient;

    @Inject
    public HentDokumentConsumer(@Value("${hentdokument.url}") String hentDokumentUrl) {
        this.hentDokumentUrl = hentDokumentUrl;
    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
    @Retryable(include = ServerErrorException.class, backoff = @Backoff(delay = 3, multiplier = 500))
    public HentDokumentResponseTo hentDokument(String journalpostId, String dokumentInfoId, String variantFormat, String token) {
        HttpHeaders httpHeaders = createAuthHeaderFromToken(token);
        log.info("Henter dokument fra saf journalpostId={}, dokumentInfoId={}, variantFormat={}", journalpostId, dokumentInfoId, variantFormat);
        byte[] dokument = webClient
                .method(HttpMethod.GET)
                .uri(this.hentDokumentUrl + "/" + journalpostId + "/" + dokumentInfoId + "/" + variantFormat)
                .headers(getHttpHeadersAsConsumer(httpHeaders))
                .header("Nav-Callid", MDC.get(MDC_CALL_ID))
                .header("Nav-Consumer-Id", "srvtilbakemeldings")
                .retrieve()
                .onStatus(HttpStatusCode::isError, statusResponse -> errorResponse(statusResponse, "saf (hent dokument)"))
                .bodyToMono(byte[].class)
                .block();
        return mapResponse(dokument, journalpostId, dokumentInfoId, variantFormat);

    }

    private HentDokumentResponseTo mapResponse(byte[] dokument, String journalpostId, String dokumentInfoId, String variantFormat) {
        try {
            return HentDokumentResponseTo.builder()
                    .dokument(dokument)
                    .build();
        } catch (Exception e) {
            throw new ServerErrorException(String.format("Kunne ikke dekode dokument, da dokumentet ikke er base64-encodet journalpostId=%s, dokumentInfoId=%s, variantFormat=%s. Feilmelding=%s", journalpostId, dokumentInfoId, variantFormat, e
                    .getMessage()), e);
        }
    }

    private Consumer<HttpHeaders> getHttpHeadersAsConsumer(HttpHeaders httpHeaders) {
        return consumer -> {
            consumer.addAll(httpHeaders);
        };
    }

    private Mono<Throwable> errorResponse(ClientResponse statusResponse, String tjeneste) {

        if (statusResponse.statusCode().is4xxClientError()) {
            if (statusResponse.statusCode().value() == 403 || statusResponse.statusCode().value() == 401) {
                return statusResponse.bodyToMono(String.class).flatMap(body ->
                        Mono.error(new ClientErrorUnauthorizedException(String.format("Autentisering mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_UNAUTHORIZED))
                );
            }

            log.error("Kall mot {} feilet med statuskode {}", tjeneste, statusResponse.statusCode());

            return statusResponse.bodyToMono(String.class).flatMap(body ->
                    Mono.error(new ClientErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_ERROR))
            );
        }

        return statusResponse.bodyToMono(String.class).flatMap(body ->
                Mono.error(new ServerErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_ERROR))
        );
    }

}
