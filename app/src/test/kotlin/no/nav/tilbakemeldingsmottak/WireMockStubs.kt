package no.nav.tilbakemeldingsmottak

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.patch
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.http.ContentTypeHeader
import no.nav.tilbakemeldingsmottak.TestUtils.createNorg2Response
import no.nav.tilbakemeldingsmottak.TestUtils.createSafGraphqlResponse
import org.springframework.http.MediaType

object WireMockStubs {

    fun stubTokenEndpoint() {
        stubFor(
            post(urlEqualTo("/fake/token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "access_token": "mocked-token",
                              "token_type": "Bearer",
                              "expires_in": 3600
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    fun stubPDL() {
        stubFor(
            post(urlEqualTo("/pdl/query"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"data": {"hentPerson": {"navn": []}}}""")
                )
        )
    }

    fun stubEreg() {
        stubFor(
            get(urlPathMatching("/ereg/v1/organisasjon/[0-9]*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("hei")
                )
        )
    }

    fun stubForEregScenario() {
        stubFor(
            get(urlPathMatching("/ereg/v1/organisasjon/[0-9]*"))
                .inScenario("opprett_serviceklage").whenScenarioStateIs("ereg_404")
                .willReturn(
                    aResponse().withStatus(404) // NOT_FOUND
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("not_found")
                )
        )
    }

    fun stubForJoark() {
        stubFor(
            post(urlPathMatching("/OPPRETT_JOURNALPOST/journalpost.*"))
                .willReturn(
                    aResponse().withStatus(201) // CREATED
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("joark/opprettJournalpost/opprettJournalpostResponse.json")
                )
        )
    }

    fun stubForOpprettJournalpostFail() {
        stubFor(
            post(urlPathMatching("/OPPRETT_JOURNALPOST/journalpost.*"))
                .inScenario("opprett_journalpost").whenScenarioStateIs("opprett_journalpost_500")
                .willReturn(
                    aResponse().withStatus(500)
                )
        )
    }

    fun stubForOpprettOppgave() {
        stubFor(
            post(urlPathMatching("/OPPGAVE"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/opprettOppgaveResponse.json")
                )
        )
    }

    fun stubForEndreOppgave() {
        stubFor(
            patch(urlPathMatching("/OPPGAVE/[0-9]*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Oppgave endret")
                )
        )
    }

    fun stubForHenteOppgaveIngenJournalpost() {
        stubFor(
            get(urlPathMatching("/OPPGAVE/[8]*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIkkeEksisterendeJournalpostResponse.json")
                )
        )
    }

    fun stubForHentOppgaveUtenJournalpost() {
        stubFor(
            get(urlPathMatching("/OPPGAVE/[9]*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIngenJournalpostSattResponse.json")
                )
        )
    }

    fun stubForHentOppgave() {
        stubFor(
            get(urlPathMatching("/OPPGAVE/[0-7]*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveResponse.json")
                )
        )
    }

    fun stubForPdlHentIdenter() {
        stubFor(
            post(urlPathMatching("/pdlgraphql"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("pdl/hentIdenterResponse.json")
                )
        )
    }

    fun stubForNorg2HenEnhet() {
        stubFor(
            get(urlPathMatching("/norg2/enhet"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createNorg2Response())
                )
        )
    }

    fun stubForSafHentJournalpost() {
        stubFor(
            post(urlPathMatching("/safgraphql"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createSafGraphqlResponse())
                    //.withBodyFile("saf/hentJournalpostResponse.json")

                )
        )
    }

    fun stubForHentDokument() {
        stubFor(
            get(urlPathMatching("/hentdokument/.*/.*/.*"))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")
                )
        )
    }

    fun stubForHentDokumentScenario() {
        stubFor(
            get(urlPathMatching("/hentdokument/.*/.*/.*"))
                .inScenario("hent_dokument").whenScenarioStateIs("saf_403")
                .willReturn(
                    aResponse().withStatus(403) // FORBIDDEN
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")
                )
        )
    }

    fun stubForHentToken() {
        stubFor(
            post(urlEqualTo("/fake/token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                    {
                      "access_token": "mocked-obo-access-token",
                      "token_type": "Bearer",
                      "expires_in": 3600
                    }
                    """.trimIndent()
                        )
                )
        )
    }


}