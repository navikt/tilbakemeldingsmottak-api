package no.nav.tilbakemeldingsmottak.metrics

import io.micrometer.core.annotation.Incubating
import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.Timer
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method
import java.util.*
import java.util.function.Function

@Aspect
@Incubating(since = "1.0.0")
class DokTimedAspect private constructor(
    private val registry: MeterRegistry,
    private val tagsBasedOnJoinpoint: Function<ProceedingJoinPoint, Iterable<Tag>>
) {
    constructor(registry: MeterRegistry, oidcUtils: OidcUtils) : this(
        registry,
        Function<ProceedingJoinPoint, Iterable<Tag>> { pjp: ProceedingJoinPoint ->
            Tags.of(
                "class", pjp.staticPart.signature.declaringTypeName,
                "method", pjp.staticPart.signature.name
            )
        }
    )

    @Around("execution (@no.nav.tilbakemeldingsmottak.metrics.Metrics * *.*(..))")
    fun incrementMetrics(pjp: ProceedingJoinPoint): Any {
        val method = (pjp.signature as MethodSignature).method
        val metrics = Objects.requireNonNull(AnnotationUtils.getAnnotation(method, Metrics::class.java))
        if (metrics?.value?.isEmpty() == true) {
            return pjp.proceed()
        }
        val sample = Timer.start(registry)
        return try {
            pjp.proceed()
        } catch (e: Exception) {
            Counter.builder((metrics?.value ?: "") + "_exception")
                .tags("error_type", if (isFunctionalException(method, e)) "functional" else "technical")
                .tags("exception_name", e.javaClass.getSimpleName())
                .tags(*metrics?.extraTags ?: emptyArray())
                .tags(tagsBasedOnJoinpoint.apply(pjp))
                .register(registry)
                .increment()
            throw e
        } finally {
            sample.stop(
                Timer.builder(metrics?.value ?: "")
                    .description(metrics?.description?.ifEmpty { null })
                    .tags(*metrics?.extraTags ?: emptyArray())
                    .tags(tagsBasedOnJoinpoint.apply(pjp))
                    .publishPercentileHistogram(metrics?.histogram)
                    .publishPercentiles(*((if (metrics?.percentiles?.isEmpty() == true) null else metrics?.percentiles)!!))
                    .register(registry)
            )
        }
    }


    private fun isFunctionalException(method: Method, e: Exception): Boolean {
        return listOf(*method.exceptionTypes).contains(e.javaClass) || isFunctionalException(e)
    }

    private fun isFunctionalException(e: Throwable): Boolean {
        return e is ClientErrorException
    }
}
