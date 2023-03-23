package no.nav.tilbakemeldingsmottak.consumer.pdl;


import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.graphql.IdentGruppe;
import no.nav.tilbakemeldingsmottak.graphql.IdentInformasjon;
import no.nav.tilbakemeldingsmottak.graphql.Identliste;
import no.nav.tilbakemeldingsmottak.graphql.util.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import java.util.List;

// Relevante linker:
// https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_spring#the-spring-conf
// https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-client
@Service
@RequiredArgsConstructor
public class PdlService {
    private final QueryExecutor queryExecutor;

    public Identliste hentIdenter(String ident, List<IdentGruppe> grupper, Boolean historikk) {
//        QueryExecutor queryExecutor = new QueryExecutor("halla", (Client) webClient);

        try {
            return queryExecutor.hentIdenter("{identer {ident gruppe historisk}}", ident, grupper, historikk);
        } catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException e) {
            throw new RuntimeException(e);
        }
    }

    public String hentAktorIdForIdent(String ident) {
        List<IdentInformasjon> identer = hentIdenter(ident, List.of(IdentGruppe.AKTORID), false).getIdenter();
        if (identer.isEmpty()) {
            throw new RuntimeException("Fant ingen aktørId for ident");
        }
        return identer.get(0).getIdent();
    }

    // https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_oauth2
//    @Bean
//    ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
//            ReactiveClientRegistrationRepository clientRegistrations) {
//        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
//                clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
//        oauth.setDefaultClientRegistrationId("pdl");
//        return oauth;
//    }


}
