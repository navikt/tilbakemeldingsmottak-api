package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil.createAuthHeaderFromToken;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.sts.STSRestConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafHentDokumentFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafHentDokumentTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * @author Sigurd Midttun, Visma Consulting.
 */
@Slf4j
@Component
public class HentDokumentConsumer implements HentDokument {

	@Inject
	@Qualifier("safclient")
	private WebClient webClient;

	private final String hentDokumentUrl;
	private final STSRestConsumer stsRestConsumer;

	@Inject
	public HentDokumentConsumer(@Value("${hentdokument.url}") String hentDokumentUrl,
								STSRestConsumer stsRestConsumer) {
		this.hentDokumentUrl = hentDokumentUrl;
		this.stsRestConsumer = stsRestConsumer;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
	@Retryable(include = AbstractTilbakemeldingsmottakTechnicalException.class, backoff = @Backoff(delay = 3, multiplier = 500))
	public HentDokumentResponseTo hentDokument(String journalpostId, String dokumentInfoId, String variantFormat, String token) {
		HttpHeaders httpHeaders = createAuthHeaderFromToken(token);
		byte[] dokument = webClient
				.method(HttpMethod.GET)
				.uri(this.hentDokumentUrl + "/"+journalpostId+"/" +dokumentInfoId + "/" + variantFormat)
				.headers(getHttpHeadersAsConsumer(httpHeaders))
				.header("Nav-Callid", MDC.get(MDC_CALL_ID))
				.header("Nav-Consumer-Id", "srvtilbakemeldings")
				.retrieve()
				.onStatus(HttpStatus::isError, statusResponse -> {
					log.error(String.format("Kall mot saf feilet med statusKode=%s", statusResponse.statusCode()));
					if (statusResponse.statusCode().is5xxServerError()) {
						throw new SafHentDokumentTechnicalException(String.format("Kall mot saf:hentdokument feilet teknisk med statusKode=%s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					} else if (statusResponse.statusCode().is4xxClientError()) {
						throw new SafHentDokumentFunctionalException(String.format("Kall mot saf:hentdokument feilet teknisk med statusKode=%s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					}
					return Mono.error(new IllegalStateException(
							String.format("Kall mot saf feilet med statusKode=%s", statusResponse.statusCode())));

				})
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
			throw new SafHentDokumentFunctionalException(String.format("Kunne ikke dekode dokument, da dokumentet ikke er base64-encodet journalpostId=%s, dokumentInfoId=%s, variantFormat=%s. Feilmelding=%s", journalpostId, dokumentInfoId, variantFormat, e
					.getMessage()), e);
		}
	}

	private Consumer<HttpHeaders> getHttpHeadersAsConsumer(HttpHeaders httpHeaders) {
		return consumer -> {
			consumer.addAll(httpHeaders);
		};
	}

}
