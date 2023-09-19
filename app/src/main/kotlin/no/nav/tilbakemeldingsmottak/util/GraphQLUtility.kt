package no.nav.tilbakemeldingsmottak.util

import com.expediagroup.graphql.client.types.GraphQLClientError
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("no.nav.soknad.innsending.consumerapis.GrapQLUtility")

fun handleErrors(errors: List<GraphQLClientError>, system: String) {
    val errorMessage = errors
        .map { "${it.message} (feilkode: ${it.path} ${it.path?.forEach { e -> e.toString() }}" }
        .joinToString(prefix = "Error i respons fra $system: ", separator = ", ") { it }
    logger.error("Oppslag mot $system feilet med $errorMessage")
    throw ServerErrorException("Oppslag mot $system feilet. Fikk feil i responsen fra $system")
}
