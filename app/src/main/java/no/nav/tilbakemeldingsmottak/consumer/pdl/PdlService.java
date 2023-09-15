package no.nav.tilbakemeldingsmottak.consumer.pdl;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
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

    public Identliste hentIdenter(String ident, List<IdentGruppe> grupper) throws ClientErrorException {
        try {
            return queryExecutor.hentIdenter("{identer {ident gruppe historisk}}", ident, grupper, false);
        } catch (GraphQLRequestExecutionException | GraphQLRequestPreparationException e) {
            throw new ClientErrorException("Graphql query mot PDL feilet", e, ErrorCode.PDL_ERROR);
        } catch (Exception ex) {
            throw new ClientErrorException("Ukjent feil mot PDL", ex, ErrorCode.PDL_ERROR);
        }
    }

    public String hentAktorIdForIdent(String ident) throws ClientErrorException {
        List<IdentInformasjon> identer = hentIdenter(ident, List.of(IdentGruppe.AKTORID)).getIdenter();

        if (identer == null || identer.isEmpty()) {
            throw new ClientErrorException("Fant ingen aktørId for ident", ErrorCode.PDL_MISSING_AKTORID);
        }

        return identer.get(0).getIdent();
    }

}
