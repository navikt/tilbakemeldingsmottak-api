package no.nav.tilbakemeldingsmottak.consumer.saf

import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.GraphQLRequest
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.JournalpostToMapper
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.JournalpostToValidator
import no.nav.tilbakemeldingsmottak.consumer.saf.graphql.SafGraphqlConsumer
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class SafJournalpostQueryServiceImpl(private val safGraphqlConsumer: SafGraphqlConsumer) : SafJournalpostQueryService {
    private val journalpostMapper = JournalpostToMapper()
    private val journalpostToValidator = JournalpostToValidator()

    private val log = LoggerFactory.getLogger(javaClass)
    private val JOURNALPOST_QUERY = "query journalpost(\$queryJournalpostId: String!) {\n" +
            "  journalpost(journalpostId: \$queryJournalpostId) {\n" +
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
            "}\n"

    override fun hentJournalpost(journalpostid: String): Journalpost {
        log.info("Henter journalpost med dokumentliste for journalpostId: $journalpostid")
        val journalpost =
            journalpostToValidator.validateAndReturn(
                safGraphqlConsumer.performQuery(
                    GraphQLRequest(
                        JOURNALPOST_QUERY,
                        "journalpost",
                        Collections.singletonMap("queryJournalpostId", journalpostid)
                    )
                )
            )
        log.info("Hentet journalpost med journalpostId: $journalpostid")
        return journalpost
    }


}