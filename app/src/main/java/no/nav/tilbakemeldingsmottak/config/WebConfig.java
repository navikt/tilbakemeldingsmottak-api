package no.nav.tilbakemeldingsmottak.config;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.tilbakemeldingsmottak.interceptors.MDCPopulationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.inject.Inject;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final OIDCRequestContextHolder oidcRequestContextHolder;

	@Inject
	public WebConfig(OIDCRequestContextHolder oidcRequestContextHolder) {
		this.oidcRequestContextHolder = oidcRequestContextHolder;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MDCPopulationInterceptor(oidcRequestContextHolder))
				.addPathPatterns("/rest/serviceklage");
	}
}
