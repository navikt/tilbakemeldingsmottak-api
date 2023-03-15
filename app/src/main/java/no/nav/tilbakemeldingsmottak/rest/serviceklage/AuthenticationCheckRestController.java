package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Unprotected;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import no.nav.tilbakemeldingsmottak.api.AuthenticationCheckRestControllerApi;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@Slf4j
@Unprotected
@RestController
@RequiredArgsConstructor
public class AuthenticationCheckRestController implements AuthenticationCheckRestControllerApi {

    private final OidcUtils oidcUtils;

    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<Boolean>
    isAuthenticated(@CookieValue(name = "isso-idtoken", required = false) String innlogget)
        {

        log.info("authenticationTest");
        boolean loggetInn = oidcUtils.getSubject(innlogget) != null ? true : oidcUtils.getSubjectForFirstValidToken().isPresent();
        if (loggetInn) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(loggetInn);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(loggetInn);
        }

    }

}
