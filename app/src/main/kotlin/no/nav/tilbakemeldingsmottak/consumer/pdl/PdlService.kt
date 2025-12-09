package no.nav.tilbakemeldingsmottak.consumer.pdl

import no.nav.tilbakemeldingsmottak.consumer.pdl.domain.IdentDto
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PdlService(private val pdlClient: PdlClient) {

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
        return pdlClient.performQuery(brukerId)?.hentIdenter?.identer?.map {
            IdentDto(
                it.ident,
                it.gruppe.toString(),
                it.historisk
            )
        } ?: listOf(IdentDto(brukerId, "AKTORID", false))
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
