package no.nav.tilbakemeldingsmottak.consumer.pdl

import no.nav.tilbakemeldingsmottak.consumer.pdl.domain.IdentDto
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
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
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.support.RetrySynchronizationManager
import org.springframework.stereotype.Service


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
    @Cacheable("hentIdenter")
    fun hentPersonIdents(brukerId: String): List<IdentDto> {
        try {
            return hentIdenter(brukerId)?.hentIdenter?.identer?.map {
                IdentDto(
                    it.ident,
                    it.gruppe.toString(),
                    it.historisk
                )
            }
                ?: listOf(IdentDto(brukerId, "AKTORID", false))
        } catch (e: Exception) {
            throw ClientErrorException("Graphql query mot PDL feilet", e, ErrorCode.PDL_ERROR)
        }
    }

    @Retryable(include = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun hentIdenter(ident: String): HentIdenter.Result? {
        log.info("Henter identer for ident: xxxx")
        log.info("###Retry Number: " + RetrySynchronizationManager.getContext()?.retryCount);

        // This will map only `data` part to HentIdenter.Result, if data is present
        val identliste: Identliste? = try {
            pdlGraphQLClient.document(HENT_IDENTER)
                .variable("ident", ident)
                .retrieve("hentIdenter")
                .toEntity(Identliste::class.java)
                .block()
        } catch (e: GraphQlClientException) {
            log.warn("GraphQL client transport or protocol error", e)
            throw ClientErrorException("Feil ved kall til PDL", e, ErrorCode.PDL_ERROR)
        } catch (ex: Exception) {
            log.warn("Exception error when doing PDL call", ex)
            throw ClientErrorException("Feil ved kall til PDL", ex, ErrorCode.PDL_ERROR)
        }
        log.info("Hentet identer for ident: xxxx")

        // Currently no method for accessing errors, validate data
        if (identliste == null) {
            log.warn("Fant ingen aktørId for ident")
            return null
        }

        return HentIdenter.Result(hentIdenter = identliste)
    }

    @Recover
    fun retryFailed(e: Exception) {
        log.warn("Retrying failed", e)
        throw e
    }


    fun hentAktorIdForIdent(ident: String): String {
        log.info("Skal hente aktørId for ident")
        val identer = hentPersonIdents(ident)
        if (identer.isEmpty()) {
            throw ClientErrorException("Fant ingen aktørId for ident", null, ErrorCode.PDL_MISSING_AKTORID)
        }
        return identer[0].ident
    }
}
