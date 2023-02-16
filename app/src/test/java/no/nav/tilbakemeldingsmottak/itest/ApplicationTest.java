package no.nav.tilbakemeldingsmottak.itest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.microsoft.graph.models.Message;
import com.nimbusds.jose.JOSEObjectType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.mock.oauth2.token.OAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import no.nav.security.token.support.spring.test.MockLoginController;
import no.nav.tilbakemeldingsmottak.Application;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClient;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.Filter;

import java.util.*;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static no.nav.tilbakemeldingsmottak.TestUtils.*;
import static no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.LOGINSERVICE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.RESTSTS_ISSUER;


@ActiveProfiles("itest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        classes = {Application.class}
)
@ExtendWith(SpringExtension.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@AutoConfigureDataLdap
@Transactional
@EnableMockOAuth2Server(port = 1888)
@AutoConfigureWireMock()
public class ApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Inject
    protected ServiceklageRepository serviceklageRepository;
    @Inject
    protected TestRestTemplate restTemplate;
    @Autowired
    MockOAuth2Server mockOAuth2Server;

    @Autowired
    AADMailClient emailService;

    @Value("${local.server.port}")
    private int serverPort;

    @Captor
    ArgumentCaptor<Message> messageCaptor;

    protected static final String CONSUMER_ID = "theclientid";
    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";
    private static final String SRVUSER = "srvtilbakelendingse";
    private static final String INNLOGGET_BRUKER = "14117119611";
    private static final String AUD ="aud-localhost";



    @BeforeEach
    void setup() {

        serviceklageRepository.deleteAll();
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.CREATED.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("joark/opprettJournalpost/opprettJournalpostResponse.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/ereg/v1/organisasjon/[0-9]*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("hei")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPGAVE"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/opprettOppgaveResponse.json")));

        WireMock.stubFor(WireMock.patch(WireMock.urlPathMatching("/OPPGAVE/[0-9]*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Oppgave endret")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[8]*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIkkeEksisterendeJournalpostResponse.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[9]*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIngenJournalpostSattResponse.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[0-7]*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveResponse.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/STS"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(String.format("{\"accessToken\": \"%s\", \"token_type\": \"%s\", \"expires_in\":3600}", getToken(RESTSTS_ISSUER, SRVUSER), "Bearer"))));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/AKTOER/identer/"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("aktoer/aktoerResponse.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/norg2/enhet"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createNorg2Response())));

/*
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/safgraphql")).withRequestBody(containing("queryJournalpostId:88"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createSafGraphqlNoDocumentsResponse())));

*/
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/safgraphql"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createSafGraphqlResponse())));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/hentdokument/.*/.*/.*"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")));

    }

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }

    HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(LOGINSERVICE_ISSUER, INNLOGGET_BRUKER));
        headers.add("correlation_id", UUID.randomUUID().toString());
        return headers;
    }

    HttpHeaders createHeaders(String issuer, String user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(issuer, user));
        headers.add("correlation_id", UUID.randomUUID().toString());
        return headers;
    }

    HttpHeaders createHeaders(String issuer, String user, Boolean addCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = getToken(issuer, user);

        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(issuer, user));
        headers.add("correlation_id", UUID.randomUUID().toString());
        if (addCookie) {
            headers.add("Cookie", "selvbetjening-idtoken="+token);
        }
        return headers;
    }


    private String getToken(String user) {
        return token(AZURE_ISSUER, user, AUD);
    }

    public String getToken(String issuer, String user) {
        return token(issuer, user, AUD);
    }


    private String token(String issuerId, String subject, String audience)  {
        OAuth2TokenCallback oAuth2TokenCallback = new DefaultOAuth2TokenCallback(
                issuerId,
                subject,
                JOSEObjectType.JWT.getType(),
                List.of(audience),
                Map.of("acr","Level4"),
                3600
        );
        return mockOAuth2Server.issueToken(
                issuerId,
                MockLoginController.class.getSimpleName(),
                oAuth2TokenCallback
            ).serialize();
    }


}
