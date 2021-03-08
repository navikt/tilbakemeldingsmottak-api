package no.nav.tilbakemeldingsmottak.config;

import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Configuration
public class RestConfig {
	@Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
                              ServiceuserAlias serviceuserAlias,
                              ClientHttpRequestFactory requestFactory) {
		return restTemplateBuilder
				.requestFactory(() -> requestFactory)
				.basicAuthentication(serviceuserAlias.getUsername(), serviceuserAlias.getPassword())
				.interceptors((request, body, execution)->{
					request.getHeaders().add(MDCConstants.MDC_CALL_ID, MDC.get(MDCConstants.MDC_CALL_ID));
					return execution.execute(request, body);
				})
				.setConnectTimeout(Duration.ofSeconds(10))
				.setReadTimeout(Duration.ofSeconds(10)).build();
	}

	@Bean
    ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	HttpClient httpClient() {
		return HttpClients.createDefault();
	}
}