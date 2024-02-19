package no.nav.tilbakemeldingsmottak.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import org.springframework.stereotype.Component

@Component
class MetricsUtils(private val registry: MeterRegistry) {

    fun incrementNotLoggedInRequestCounter(className: String, method: String) {
        Counter.builder(DOK_REQUEST + "_not_logged_in")
            .description("Number of not logged in user requests")
            .tag("class", className)
            .tag("method", method)
            .register(registry)
            .increment()
    }

}