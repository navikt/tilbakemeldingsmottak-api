package no.nav.tilbakemeldingsmottak.nais;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import no.nav.security.token.support.core.api.Unprotected;
import no.nav.tilbakemeldingsmottak.model.SelfCheckResult;
import no.nav.tilbakemeldingsmottak.model.SelftestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Unprotected
public class NaisContract {

    public static final String APPLICATION_ALIVE = "Application is alive!";
    public static final String APPLICATION_READY = "Application is ready for traffic!";
    private static final String APPLICATION_NOT_READY = "Application is not ready for traffic :-(";

    private final String appName;
    private final String version;

    private final AtomicInteger app_status = new AtomicInteger();

    @Inject
    public NaisContract(
            MeterRegistry registry,
            @Value("${APP_NAME:tilbakemeldingsmottak}") String appName,
            @Value("${APP_VERSION:0}") String version
    ) {
        Gauge.builder("dok_app_is_ready", app_status, AtomicInteger::get).register(registry);
        this.appName = appName;
        this.version = version;
    }

    @GetMapping("/isAlive")
    public String isAlive() {
        return APPLICATION_ALIVE;
    }

    @RequestMapping(value = "/isReady", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity isReady() {
        app_status.set(1);
        return new ResponseEntity<>(APPLICATION_READY, HttpStatus.OK);
    }

    @GetMapping("/internal/selftest")
    public @ResponseBody
    SelftestResult selftest() {
        return new SelftestResult(appName, version, SelfCheckResult.OK, Collections.emptyList());
    }

}
