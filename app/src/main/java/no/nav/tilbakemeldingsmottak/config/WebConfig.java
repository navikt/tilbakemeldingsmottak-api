package no.nav.tilbakemeldingsmottak.config;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.interceptors.MDCPopulationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MDCPopulationInterceptor())
				.addPathPatterns("/rest/**");
    }
}
