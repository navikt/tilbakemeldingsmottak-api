package no.nav.tilbakemeldingsmottak.itest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import no.nav.security.spring.oidc.test.TokenGeneratorController;
import no.nav.tilbakemeldingsmottak.CoreConfig;
import no.nav.tilbakemeldingsmottak.config.RepositoryConfig;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {CoreConfig.class, RepositoryConfig.class})
@ActiveProfiles("itest")
@AutoConfigureWireMock(port = 0)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@AutoConfigureCache
@AutoConfigureDataLdap
@Transactional
public class AbstractIT {

    @Inject
    protected ServiceklageRepository serviceklageRepository;
    @Inject
    protected TestRestTemplate restTemplate;

    protected GreenMail smtpServer;
    private int port = 2500;

    protected static final String JOURNALPOST_ID = "12345";

    @BeforeEach
    void setup() {
        serviceklageRepository.deleteAll();
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        smtpServer = new GreenMail(new ServerSetup(port, null, "smtp"));
        smtpServer.start();

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/"))
            .willReturn(WireMock.aResponse().withStatus(HttpStatus.CREATED.value())
                    .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("joark/opprettJournalpost/opprettJournalpostResponse.json")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPGAVE"))
            .willReturn(WireMock.aResponse().withStatus(HttpStatus.CREATED.value())
                    .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody("Oppgave opprettet")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/STS"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(String.format("{\"accessToken\": \"%s\", \"token_type\": \"%s\", \"expires_in\":3600}", getToken("srvtilbakemeldingsmot"), "Bearer"))));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/AKTOER/identer/"))
            .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                    .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("aktoer/aktoerResponse.json")));

    }

    @AfterEach
    void tearDown() {
        smtpServer.stop();
    }

    HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken("srvconsumer"));
        return headers;
    }

    private String getToken(String user) {
        TokenGeneratorController tokenGeneratorController = new TokenGeneratorController();
        return tokenGeneratorController.issueToken(user);
    }

}
