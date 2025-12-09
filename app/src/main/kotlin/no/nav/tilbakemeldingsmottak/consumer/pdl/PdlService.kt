package no.nav.tilbakemeldingsmottak.consumer.pdl

import io.grpc.netty.shaded.io.netty.handler.timeout.ReadTimeoutException
import no.nav.tilbakemeldingsmottak.consumer.pdl.domain.IdentDto
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.pdl.generated.HENT_IDENTER
import no.nav.tilbakemeldingsmottak.pdl.generated.HentIdenter
import no.nav.tilbakemeldingsmottak.pdl.generated.hentidenter.Identliste
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.graphql.client.GraphQlClientException
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.support.RetrySynchronizationManager
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.function.Consumer


@Service
class PdlService(@Qualifier("pdlQlClient") private val pdlGraphQLClient: HttpGraphQlClient) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${pdl.url}")
    lateinit var pdlUrl: String

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "hentAktoerIdForIdent"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun hentPersonIdents(brukerId: String): List<IdentDto> {
        return performQuery(brukerId)?.hentIdenter?.identer?.map {
            IdentDto(
                it.ident,
                it.gruppe.toString(),
                it.historisk
            )
        } ?: listOf(IdentDto(brukerId, "AKTORID", false))
    }

    @Retryable(include = [ServerErrorException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun performQuery(ident: String): HentIdenter.Result? {
        log.info("Henter identer for ident: xxxx")
        log.info("###Retry Number: " + RetrySynchronizationManager.getContext()?.retryCount);

        // This will map only `data` part to HentIdenter.Result, if data is present
        val identliste: Identliste? =
            pdlGraphQLClient.document(HENT_IDENTER)
                .variable("ident", ident)
                .retrieve("hentIdenter")
                .toEntity(Identliste::class.java)
                .doOnError(Consumer { error: Throwable -> handleError(error, "PDL hentIdenter") })
                .block()
        log.info("Hentet identer for ident: xxxx")

        // Currently no method for accessing errors, validate data
        if (identliste == null) {
            log.warn("Fant ingen aktørId for ident")
            throw ClientErrorException(
                message = "Ingen aktorid funnet for ident",
                errorCode = ErrorCode.PDL_MISSING_AKTORID
            )
        }

        return HentIdenter.Result(hentIdenter = identliste)
    }

    private fun handleError(error: Throwable, serviceName: String) {
        if (error is WebClientResponseException) {
            val statusCode: HttpStatusCode = error.statusCode
            val responseBody: String = error.responseBodyAsString
            val errorMessage =
                String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)
            if (statusCode === HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.PDL_ERROR)
            }
            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.PDL_ERROR)
            }
            throw ServerErrorException(errorMessage, error, ErrorCode.PDL_ERROR)
        } else if (error is ReadTimeoutException) {
            log.warn("Fant ingen aktørId for ident pga ReadTimeout")
            throw ServerErrorException("ReadMessageException", error, ErrorCode.PDL_ERROR)
        }
    }

    @Recover
    fun retryFailed(e: Exception) {
        log.warn("Retrying failed", e)
        throw e
    }


    @Cacheable("hentIdenter")
    fun hentAktorIdForIdent(ident: String): String {
        log.info("Skal hente aktørId for ident")
        val identer = hentPersonIdents(ident)
        if (identer.isEmpty()) {
            throw ClientErrorException("Fant ingen aktørId for ident", null, ErrorCode.PDL_MISSING_AKTORID)
        }
        return identer[0].ident
    }
}
