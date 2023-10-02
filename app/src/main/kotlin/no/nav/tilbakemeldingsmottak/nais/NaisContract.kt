package no.nav.tilbakemeldingsmottak.nais

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.token.support.core.api.Unprotected
import no.nav.tilbakemeldingsmottak.model.SelfCheckResult
import no.nav.tilbakemeldingsmottak.model.SelftestResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

@RestController
@Unprotected
class NaisContract(
    registry: MeterRegistry,
    @Value("\${APP_NAME:tilbakemeldingsmottak}") private val appName: String,
    @Value("\${APP_VERSION:0}") private val version: String
) {

    private val APPLICATION_ALIVE = "Application is alive!"
    private val APPLICATION_READY = "Application is ready for traffic!"
    private val appStatus = AtomicInteger()

    init {
        Gauge.builder("dok_app_is_ready", appStatus) { it.get().toDouble() }.register(registry)
    }


    @GetMapping("/isAlive")
    fun isAlive(): String {
        return APPLICATION_ALIVE
    }

    @RequestMapping(value = ["/isReady"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isReady(): ResponseEntity<String> {
        appStatus.set(1)
        return ResponseEntity(APPLICATION_READY, HttpStatus.OK)
    }

    @GetMapping("/internal/selftest")
    fun selftest(): SelftestResult {
        return SelftestResult(
            appName = appName,
            version = version,
            result = SelfCheckResult.OK,
            dependencyCheckResults = emptyList()
        )
    }
}
