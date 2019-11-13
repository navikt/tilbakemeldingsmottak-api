package no.nav.tilbakemeldingsmottak.metrics;

import static java.util.Arrays.asList;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.NonNullApi;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

@Aspect
@NonNullApi
@Incubating(since = "1.0.0")
@Slf4j
public class DokTimedAspect {

	private final MeterRegistry registry;
	private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinpoint;

	public DokTimedAspect(MeterRegistry registry) {
		this(registry, pjp ->
				Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(),
						"method", pjp.getStaticPart().getSignature().getName())
		);
	}

	private DokTimedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinpoint) {
		this.registry = registry;
		this.tagsBasedOnJoinpoint = tagsBasedOnJoinpoint;
	}

	@Around("execution (@no.nav.tilbakemeldingsmottak.metrics.Metrics * *.*(..))")
	public Object incrementMetrics(ProceedingJoinPoint pjp) throws Throwable {
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		Metrics metrics = Objects.requireNonNull(AnnotationUtils.getAnnotation(method, Metrics.class));
		if (metrics.value().isEmpty()) {
			return pjp.proceed();
		}

		Timer.Sample sample = Timer.start(registry);
		try {
			return pjp.proceed();
		} catch (Exception e) {

			Counter.builder(metrics.value() + "_exception")
					.tags("error_type", isFunctionalException(method, e) ? "functional" : "technical")
					.tags("exception_name", e.getClass().getSimpleName())
					.tags(metrics.extraTags())
					.tags(tagsBasedOnJoinpoint.apply(pjp))
					.register(registry)
					.increment();

			throw e;

		} finally {
			sample.stop(Timer.builder(metrics.value())
					.description(metrics.description().isEmpty() ? null : metrics.description())
					.tags(metrics.extraTags())
					.tags(tagsBasedOnJoinpoint.apply(pjp))
					.publishPercentileHistogram(metrics.histogram())
					.publishPercentiles(metrics.percentiles().length == 0 ? null : metrics.percentiles())
					.register(registry));
		}
	}

	private boolean isFunctionalException(Method method, Exception e) {
		return asList(method.getExceptionTypes()).contains(e.getClass()) || isFunctionalException(e);
	}

	private boolean isFunctionalException(Throwable e) {
		return e instanceof AbstractTilbakemeldingsmottakFunctionalException;
	}
}
