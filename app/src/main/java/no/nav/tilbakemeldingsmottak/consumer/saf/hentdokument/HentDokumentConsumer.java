package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument;

import static no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil.createAuthHeaderFromToken;

import no.nav.tilbakemeldingsmottak.consumer.sts.STSRestConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafHentDokumentFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafHentDokumentTechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author Sigurd Midttun, Visma Consulting.
 */
@Component
public class HentDokumentConsumer implements HentDokument {

	private final String hentDokumentUrl;
	private final RestTemplate restTemplate;
	private final STSRestConsumer stsRestConsumer;

	@Inject
	public HentDokumentConsumer(@Value("${hentdokument.url}") String hentDokumentUrl,
								RestTemplateBuilder restTemplateBuilder,
								STSRestConsumer stsRestConsumer) {
		this.hentDokumentUrl = hentDokumentUrl;
		this.stsRestConsumer = stsRestConsumer;
		this.restTemplate = restTemplateBuilder
				.setReadTimeout(Duration.ofSeconds(20))
				.setConnectTimeout(Duration.ofSeconds(5))
				.build();
	}

	@Retryable(include = AbstractTilbakemeldingsmottakTechnicalException.class, backoff = @Backoff(delay = 3, multiplier = 500))
	public HentDokumentResponseTo hentDokument(String journalpostId, String dokumentInfoId, String variantFormat, String token) {
		try {
			HttpEntity entity = new HttpEntity<>(createAuthHeaderFromToken(token));
			byte[] dokument = restTemplate.exchange(this.hentDokumentUrl + "/{journalpostId}/{dokumentInfoId}/{variantFormat}", HttpMethod.GET, entity, byte[].class, journalpostId, dokumentInfoId, variantFormat).getBody();

			return mapResponse(dokument, journalpostId, dokumentInfoId, variantFormat);

		} catch (HttpClientErrorException e) {
			throw new SafHentDokumentFunctionalException(String.format("Kall mot saf:hentdokument feilet funksjonelt med statusKode=%s, feilmelding=%s", e
					.getStatusCode(), e.getResponseBodyAsString()), e);
		} catch (HttpServerErrorException e) {
			throw new SafHentDokumentTechnicalException(String.format("Kall mot saf:hentdokument feilet teknisk med statusKode=%s, feilmelding=%s", e
					.getStatusCode(), e.getResponseBodyAsString()), e);
		}
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
}
