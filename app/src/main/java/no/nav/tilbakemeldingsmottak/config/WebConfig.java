package no.nav.tilbakemeldingsmottak.config;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.interceptors.LoginRedirectInterceptor;
import no.nav.tilbakemeldingsmottak.interceptors.MDCPopulationInterceptor;
import no.nav.tilbakemeldingsmottak.interceptors.TokenCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final TokenCheckInterceptor tokenCheckInterceptor;
	private final LoginRedirectInterceptor loginRedirectInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MDCPopulationInterceptor())
				.addPathPatterns("/rest/**");

		registry.addInterceptor(loginRedirectInterceptor)
				.addPathPatterns("/", "/serviceklage/**");

        registry.addInterceptor(tokenCheckInterceptor)
                .addPathPatterns("/rest/serviceklage", "/rest/serviceklage/",
						"/rest/ros**",
						"/rest/feil-og-mangler**",
						"/rest/bestilling-av-samtale**");
    }
}
