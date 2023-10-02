package no.nav.tilbakemeldingsmottak.config

import no.nav.tilbakemeldingsmottak.interceptors.MDCPopulationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(MDCPopulationInterceptor())
            .addPathPatterns("/rest/**")
    }
}
