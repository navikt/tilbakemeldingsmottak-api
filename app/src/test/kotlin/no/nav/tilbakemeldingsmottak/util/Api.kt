package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.*
import no.nav.tilbakemeldingsmottak.rest.common.domain.ErrorResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

class Api(val restTemplate: WebTestClient) {

    private val URL_SENDINN_SERVICEKLAGE = "/rest/serviceklage"
    private val URL_BEHANDLE_SERVICEKLAGE = "/rest/taskserviceklage"
    private val HENT_DOKUMENT = "hentdokument"
    private val HENT_SKJEMA = "hentskjema"
    private val KLASSIFISER = "klassifiser"

    fun createServiceklage(requestEntity: HttpEntity<OpprettServiceklageRequest>): ResponseEntity<OpprettServiceklageResponse> {

        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(2))
            .build()
            .post()
            .uri(URL_SENDINN_SERVICEKLAGE)
            .headers { it.addAll(requestEntity.headers) }
            .bodyValue(requestEntity.body!!)

            .exchange()
            .expectStatus().isOk
            .expectBody(OpprettServiceklageResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }


    fun createServiceklageError(requestEntity: HttpEntity<OpprettServiceklageRequest>): ResponseEntity<ErrorResponse> {

        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(2))
            .build()
            .post()
            .uri(URL_SENDINN_SERVICEKLAGE)
            .headers { it.addAll(requestEntity.headers) }
            .bodyValue(requestEntity.body!!)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }


    fun createServiceklageServerError(requestEntity: HttpEntity<OpprettServiceklageRequest>): ResponseEntity<ErrorResponse> {

        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(2))
            .build()
            .post()
            .uri(URL_SENDINN_SERVICEKLAGE)
            .headers { it.addAll(requestEntity.headers) }
            .bodyValue(requestEntity.body!!)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(ErrorResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }

    fun getDocument(headers: HttpHeaders, oppgaveId: String): ResponseEntity<HentDokumentResponse> {
        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(12))
            .build()
            .get()
            .uri("$URL_BEHANDLE_SERVICEKLAGE/$HENT_DOKUMENT/$oppgaveId")
            .headers { it.addAll(headers) }
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(HentDokumentResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }


    fun getDocumentError(headers: HttpHeaders, oppgaveId: String): ResponseEntity<ErrorResponse> {
        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(12))
            .build()
            .get()
            .uri("$URL_BEHANDLE_SERVICEKLAGE/$HENT_DOKUMENT/$oppgaveId")
            .headers { it.addAll(headers) }
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }

    fun getSkjema(headers: HttpHeaders, journalpostId: String): ResponseEntity<HentSkjemaResponse> {
        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(2))
            .build()
            .get()
            .uri("$URL_BEHANDLE_SERVICEKLAGE/$HENT_SKJEMA/$journalpostId")
            .headers { it.addAll(headers) }
            .exchange()
            .expectStatus().isOk
            .expectBody(HentSkjemaResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }

    fun classifyServiceklage(
        requestEntity: HttpEntity<KlassifiserServiceklageRequest>,
        oppgaveId: String
    ): ResponseEntity<KlassifiserServiceklageResponse> {
        val result = restTemplate
            .mutate()
            .responseTimeout(Duration.ofMinutes(20))
            .build()
            .put()
            .uri("$URL_BEHANDLE_SERVICEKLAGE/$KLASSIFISER?oppgaveId=$oppgaveId")
            .headers { it.addAll(requestEntity.headers) }
            .bodyValue(requestEntity.body!!)
            .exchange()
            .expectStatus().isOk
            .expectBody(KlassifiserServiceklageResponse::class.java)
            .returnResult()

        return ResponseEntity
            .status(result.status)
            .headers(result.responseHeaders)
            .body(result.responseBody)
    }

}
