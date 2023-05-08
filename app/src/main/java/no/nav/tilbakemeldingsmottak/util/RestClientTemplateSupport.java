package no.nav.tilbakemeldingsmottak.util;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class RestClientTemplateSupport {

    private static int MAX_FILE_SIZE = 300 * 1024 * 1024;
    @Value("${pdl.url}")
    String pdlUrl;
    @Autowired
    private OAuth2AccessTokenService oAuth2AccessTokenService;
    @Autowired
    private ClientConfigurationProperties clientConfigurationProperties;

    @Bean
    @Qualifier("arkivClient")
    @Scope("prototype")
    WebClient arkivClient(
    ) {

        ClientProperties clientProperties =
                Optional.ofNullable(clientConfigurationProperties.getRegistration().get("arkiv"))
                        .orElseThrow(() -> new RuntimeException("Fant ikke konfigurering for arkiv"));

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties);
    }

    @Bean
    @Qualifier("oppgaveClient")
    @Scope("prototype")
    WebClient oppgaveClient(
    ) {

        ClientProperties clientProperties =
                Optional.ofNullable(clientConfigurationProperties.getRegistration().get("oppgave"))
                        .orElseThrow(() -> new RuntimeException("Fant ikke konfigurering for oppgave"));

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties);
    }

    @Bean
    @Qualifier("safclient")
    @Scope("prototype")
    WebClient safClientRestTemplate(
    ) {
        ClientProperties clientProperties =
                Optional.ofNullable(clientConfigurationProperties.getRegistration().get("saf-maskintilmaskin"))
                        .orElseThrow(() -> new RuntimeException("Fant ikke konfigurering for saf-maskintilmaskin"));

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties);
    }

    // Denne overskriver den genererte SpringConfiguration sin webClient (i target/generated-sources)
    // Akkurat nå støttes bare én graphql server, men det kan støttes flere ved å følge denne: https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_more_than_one_graphql_servers
    @Bean(name = "webClient")
    WebClient webClient() {

        ClientProperties clientProperties =
                Optional.ofNullable(clientConfigurationProperties.getRegistration().get("pdl"))
                        .orElseThrow(() -> new RuntimeException("Fant ikke konfigurering for pdl"));

        return buildWebClientWithUrl(buildHttpClient(5000, 60, 60), clientProperties, pdlUrl);
    }

    private HttpClient buildHttpClient(int connection_timeout, int readTimeout, int writeTimeout) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connection_timeout)
                .doOnConnected(conn -> conn
                        .addHandler(new ReadTimeoutHandler(readTimeout, TimeUnit.SECONDS))
                        .addHandler(new WriteTimeoutHandler(writeTimeout)));
    }

    private WebClient buildWebClient(HttpClient httpClient, ClientProperties clientProperties) {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(MAX_FILE_SIZE))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(bearerTokenExchange(clientProperties))
                .build();

    }

    private WebClient buildWebClientWithUrl(HttpClient httpClient, ClientProperties clientProperties, String url) {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Content-Type", "application/json").defaultUriVariables(Collections.singletonMap("url", url))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(MAX_FILE_SIZE))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(bearerTokenExchange(clientProperties))
                .build();
    }

    private WebClient buildWebClient(HttpClient httpClient) {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(MAX_FILE_SIZE))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }

    private ExchangeFilterFunction bearerTokenExchange(ClientProperties clientProperties) {
        return (clientRequest, exchangeFunction) -> {
            OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientProperties);
            ClientRequest filtered = ClientRequest.from(clientRequest)
                    .headers(headers -> headers.setBearerAuth(response.getAccessToken()))
                    .build();
            return exchangeFunction.exchange(filtered);
        };
    }

    @Bean
    @Qualifier("basicclient")
    @Scope("prototype")
    RestTemplate eregClientRestTemplate(
            RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }

}
