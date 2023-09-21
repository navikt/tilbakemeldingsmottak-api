package no.nav.tilbakemeldingsmottak.consumer.pdl

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import kotlinx.coroutines.runBlocking
import no.nav.tilbakemeldingsmottak.consumer.pdl.domain.IdentDto
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.pdl.generated.HentIdenter
import no.nav.tilbakemeldingsmottak.util.handleErrors
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PdlService(@Qualifier("pdlClient") private val pdlGraphQLClient: GraphQLWebClient) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun hentPersonIdents(brukerId: String): List<IdentDto> = runBlocking {
        log.info("Skal hente en personsidenter fra PDL")
        try {
            hentIdenter(brukerId)?.hentIdenter?.identer?.map { IdentDto(it.ident, it.gruppe.toString(), it.historisk) }
                ?: listOf(IdentDto(brukerId, "AKTORID", false))
        } catch (ex: Exception) {
            log.warn(("Henting fra PDL feilet med ${ex.message}. Returnerer pålogget ident"))
            listOf(IdentDto(brukerId, "FOLKEREGISTERIDENT", false))
        }
    }

    @Cacheable("hentIdenter")
    suspend fun hentIdenter(ident: String): HentIdenter.Result? {
        val response = pdlGraphQLClient.execute(
            HentIdenter(
                HentIdenter.Variables(ident)
            )
        )
        if (response.data != null) {
            checkForErrors(response.errors)
            // FIXME: Fjern logg
            log.info("Hentet identer: ${response.data}")
            return response.data
        } else {
            log.error("Oppslag mot personregisteret feilet. Fikk feil i kall for å hente identer fra personregisteret")
            throw ServerErrorException("Oppslag mot personregisteret feilet. Fikk feil i kallet for å hente identer fra personregisteret")
        }
    }

    private fun checkForErrors(errors: List<GraphQLClientError>?) {
        errors?.let { handleErrors(it, "Personregister") }
    }

    fun hentAktorIdForIdent(ident: String): String {
        log.info("Skal hente aktørId for ident")
        val identer = hentPersonIdents(ident)
        if (identer.isEmpty()) {
            throw ClientErrorException("Fant ingen aktørId for ident", null, ErrorCode.PDL_MISSING_AKTORID)
        }
        log.info("Returnerer: ${identer[0].ident}") // FIXME: Fjern

        return identer[0].ident
    }
}
