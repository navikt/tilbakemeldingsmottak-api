package no.nav.tilbakemeldingsmottak.consumer.pdl;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.pdl.PdlFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.pdl.PdlGraphqlException;
import no.nav.tilbakemeldingsmottak.graphql.IdentGruppe;
import no.nav.tilbakemeldingsmottak.graphql.IdentInformasjon;
import no.nav.tilbakemeldingsmottak.graphql.Identliste;
import no.nav.tilbakemeldingsmottak.graphql.util.QueryExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

// Relevante linker:
// https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_spring#the-spring-conf
// https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-client
@Service
@RequiredArgsConstructor
public class PdlService {
    private final QueryExecutor queryExecutor;

    public Identliste hentIdenter(String ident, List<IdentGruppe> grupper) throws PdlGraphqlException {
        try {
            return queryExecutor.hentIdenter("{identer {ident gruppe historisk}}", ident, grupper, false);
        } catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException e) {
            throw new PdlGraphqlException("Graphql query mot PDL feilet", new RuntimeException(e));
        }
    }

    public String hentAktorIdForIdent(String ident) throws PdlFunctionalException {
        List<IdentInformasjon> identer = hentIdenter(ident, List.of(IdentGruppe.AKTORID)).getIdenter();

        if (identer == null || identer.isEmpty()) {
            throw new PdlFunctionalException("Fant ingen aktørId for ident", new RuntimeException("Ingen aktørId"));
        }

        return identer.get(0).getIdent();
    }

}
