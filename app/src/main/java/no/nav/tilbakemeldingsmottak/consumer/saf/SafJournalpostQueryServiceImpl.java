package no.nav.tilbakemeldingsmottak.consumer.saf;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.GraphQLRequest;
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.JournalpostToMapper;
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.JournalpostToValidator;
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.SafGraphqlConsumer;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class SafJournalpostQueryServiceImpl implements SafJournalpostQueryService {

    private static final String JOURNALPOST_QUERY =
            "query journalpost($queryJournalpostId: String!) {\n" +
                    "  journalpost(journalpostId: $queryJournalpostId) {\n" +
                    "    bruker {\n" +
                    "       id \n" +
                    "      } \n" +
                    "    datoOpprettet \n" +
                    "    dokumenter {\n" +
                    "      dokumentInfoId\n" +
                    "      dokumentvarianter {\n" +
                    "        saksbehandlerHarTilgang\n" +
                    "        variantformat\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n";
    private final SafGraphqlConsumer safGraphqlConsumer;
    private final JournalpostToMapper journalpostMapper = new JournalpostToMapper();
    private final JournalpostToValidator journalpostToValidator = new JournalpostToValidator();

    public SafJournalpostQueryServiceImpl(SafGraphqlConsumer safGraphqlConsumer) {
        this.safGraphqlConsumer = safGraphqlConsumer;
    }

    public Journalpost hentJournalpost(String journalpostid, String authorizationHeader) {

        var journalpost = journalpostMapper.map(
                journalpostToValidator.validateAndReturn(
                        safGraphqlConsumer.performQuery(GraphQLRequest.builder()
                                .query(JOURNALPOST_QUERY)
                                .operationName("journalpost")
                                .variables(Collections.singletonMap("queryJournalpostId", journalpostid))
                                .build(), authorizationHeader))
        );

        log.info("Hentet journalpost med journalpostId: " + journalpostid);

        return journalpost;
    }
}