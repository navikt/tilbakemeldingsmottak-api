package no.nav.tilbakemeldingsmottak.itest;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

    @BeforeEach
    void setup() {
        serviceklageRepository.deleteAll();
        smtpServer = new GreenMail(new ServerSetup(port, null, "smtp"));
        smtpServer.start();
    }

    @AfterEach
    void tearDown() {
        smtpServer.stop();
    }

    HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken());
        return headers;
    }

    private String getToken() {
        TokenGeneratorController tokenGeneratorController = new TokenGeneratorController();
        return tokenGeneratorController.issueToken("srvtest");
    }

}
