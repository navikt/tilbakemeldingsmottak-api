package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.*
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

class Api(val restTemplate: TestRestTemplate) {

    private val URL_SENDINN_SERVICEKLAGE = "/rest/serviceklage"
    private val URL_BEHANDLE_SERVICEKLAGE = "/rest/taskserviceklage"
    private val HENT_DOKUMENT = "hentdokument"
    private val HENT_SKJEMA = "hentskjema"
    private val KLASSIFISER = "klassifiser"

    fun createServiceklage(requestEntity: HttpEntity<OpprettServiceklageRequest>): ResponseEntity<OpprettServiceklageResponse> {
        return restTemplate.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
    }

    fun getDocument(requestEntity: HttpEntity<Any?>, oppgaveId: String): ResponseEntity<HentDokumentResponse> {
        return restTemplate.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$HENT_DOKUMENT/$oppgaveId",
            HttpMethod.GET,
            requestEntity,
            HentDokumentResponse::class.java
        )
    }

    fun getSkjema(requestEntity: HttpEntity<Any?>, journalpostId: String): ResponseEntity<HentSkjemaResponse> {
        return restTemplate.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$HENT_SKJEMA/$journalpostId",
            HttpMethod.GET,
            requestEntity,
            HentSkjemaResponse::class.java
        )
    }

    fun classifyServiceklage(
        requestEntity: HttpEntity<KlassifiserServiceklageRequest>,
        oppgaveId: String
    ): ResponseEntity<KlassifiserServiceklageResponse> {
        return restTemplate.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$KLASSIFISER?oppgaveId=$oppgaveId",
            HttpMethod.PUT,
            requestEntity,
            KlassifiserServiceklageResponse::class.java
        )
    }


}
