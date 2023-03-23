package no.nav.tilbakemeldingsmottak.consumer.pdl;


import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.graphql.IdentGruppe;
import no.nav.tilbakemeldingsmottak.graphql.IdentInformasjon;
import no.nav.tilbakemeldingsmottak.graphql.Identliste;
import no.nav.tilbakemeldingsmottak.graphql.util.QueryExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.inject.Inject;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdlService {
    private final QueryExecutor queryExecutor;

    public Identliste hentIdenter(String ident, List<IdentGruppe> grupper, Boolean historikk) {
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
}
