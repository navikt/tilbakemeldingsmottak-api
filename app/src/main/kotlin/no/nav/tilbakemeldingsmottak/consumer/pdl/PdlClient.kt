package no.nav.tilbakemeldingsmottak.consumer.pdl

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.pdl.generated.HENT_IDENTER
import no.nav.tilbakemeldingsmottak.pdl.generated.HentIdenter
import no.nav.tilbakemeldingsmottak.pdl.generated.hentidenter.Identliste
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class PdlClient(
    @Qualifier("pdlQlClient") private val pdlGraphQLClient: HttpGraphQlClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // Retry på serverfeil og IO feil
    @Retryable(include = [ServerErrorException::class], maxAttempts = 3, backoff = Backoff(delay = 2000))
    fun performQuery(ident: String): HentIdenter.Result {
        log.info("PdlClient: performQuery for ident=xxxx")

        val identliste: Identliste? = pdlGraphQLClient.document(HENT_IDENTER)
            .variable("ident", ident)
            // Viktig: mappe error til riktig exception slik at Retry kan fange den
            .retrieve("hentIdenter")
            .toEntity(Identliste::class.java)
            .onErrorMap { t -> mapToPdlException(t, "PDL hentIdenter") }
            .block()

        if (identliste == null) {
            throw ClientErrorException(
                message = "Ingen aktorid funnet for ident",
                errorCode = ErrorCode.PDL_MISSING_AKTORID
            )
        }
        return HentIdenter.Result(hentIdenter = identliste)
    }

    private fun mapToPdlException(error: Throwable, serviceName: String): Throwable {
        // Sjekk ulike typer feil og mappe til Client/Server exceptions
        if (error is WebClientResponseException) {
            val statusCode = error.statusCode
            val body = error.responseBodyAsString
            val message = "Kall mot $serviceName feilet (statuskode: $statusCode). Body: $body"
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ClientErrorUnauthorizedException(message, error, ErrorCode.PDL_ERROR)
            }
            if (statusCode.is4xxClientError) {
                return ClientErrorException(message, error, ErrorCode.PDL_ERROR)
            }
            return ServerErrorException(message, error, ErrorCode.PDL_ERROR)
        }

        // ReadTimeoutException fra Netty (ikke grpc-shaded)
        if (error is io.netty.handler.timeout.ReadTimeoutException
            || error.cause is io.netty.handler.timeout.ReadTimeoutException
        ) {
            val message = "ReadTimeout mot $serviceName"
            return ServerErrorException(message, error, ErrorCode.PDL_ERROR)
        }

        // GraphQL client wrapper
        if (error is org.springframework.graphql.client.GraphQlClientException) {
            val message = "GraphQlClientException mot $serviceName: ${error.message}"
            return ServerErrorException(message, error, ErrorCode.PDL_ERROR)
        }

        // Wrap som ServerError så Retry kan trigges ved nettverksfeil
        return ServerErrorException("Ukjent feil mot $serviceName: ${error.message}", error, ErrorCode.PDL_ERROR)
    }


    @Recover
    fun retryFailed(e: Exception) {
        log.warn("Retry av PDL-kall feilet.", e)
        throw e
    }

}
