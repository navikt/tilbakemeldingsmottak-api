package no.nav.tilbakemeldingsmottak.rest.common.sts;

import static no.nav.tilbakemeldingsmottak.config.cache.CacheConfig.STS_CACHE;

import no.nav.tilbakemeldingsmottak.consumer.sts.STSResponse;
import no.nav.tilbakemeldingsmottak.consumer.sts.STSRestConsumer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class STSTokenService {

	private final STSRestConsumer stsRestConsumer;

	public STSTokenService(STSRestConsumer stsRestConsumer) {
		this.stsRestConsumer = stsRestConsumer;
	}

	@Retryable(backoff = @Backoff(delay = 500))
	@Cacheable(STS_CACHE)
	public STSResponse hentOidcToken() {
		return stsRestConsumer.getServiceuserOIDCToken().getBody();
	}
}
