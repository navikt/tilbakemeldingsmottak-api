package no.nav.tilbakemeldingsmottak.nais.selftest;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.model.DependencyCheckResult;
import no.nav.tilbakemeldingsmottak.model.DependencyCheckResult.ImportanceEnum;
import no.nav.tilbakemeldingsmottak.model.DependencyCheckResult.TypeEnum;
import no.nav.tilbakemeldingsmottak.model.SelfCheckResult;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Slf4j
@Setter
@Getter
public abstract class AbstractDependencyCheck {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final TypeEnum type;
    protected final ImportanceEnum importance;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(2800))
            .cancelRunningFuture(true)
            .build();
    private final TimeLimiter timeLimiter = TimeLimiter.of(timeLimiterConfig);
    protected String name;
    protected String address;
    private AtomicInteger dependency_status = new AtomicInteger();
    private Gauge gauge;

    public AbstractDependencyCheck(TypeEnum type, String name, String address, ImportanceEnum importance, MeterRegistry registry) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.importance = importance;
        this.circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        this.gauge = Gauge.builder("dok_dependency_ping", dependency_status, AtomicInteger::get)
                .tags("name", name)
                .tags("type", type.name())
                .tags("importance", importance.name())
                .register(registry);
    }

    protected abstract void doCheck();

    public Try<DependencyCheckResult> check() {
        final String dependencyName = this.name;
        Supplier<Future<DependencyCheckResult>> futureSupplier = () -> executor.submit(getCheckCallable());
        Callable<DependencyCheckResult> timeRestrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(dependencyName);
        Callable<DependencyCheckResult> chainedCallable = CircuitBreaker.decorateCallable(circuitBreaker, timeRestrictedCall);
        return Try.ofCallable(chainedCallable)
                .onSuccess(success -> dependency_status.set(1))
                .onFailure(throwable -> {
                    dependency_status.set(0);
                    log.error("Call to dependency={} with type={} at url={} timed out or circuitbreaker was tripped.", getName(), getType(), getAddress(), throwable);
                })
                .recover(throwable -> DependencyCheckResult.builder()
                        .endpoint(getName())
                        .address(getAddress())
                        .type(getType())
                        .importance(getImportance())
                        .result(getImportance().equals(ImportanceEnum.CRITICAL) ? SelfCheckResult.ERROR : SelfCheckResult.WARNING)
                        .errorMessage("Call to dependency=" + getName() + " timed out or circuitbreaker tripped. ErrorMessage=" + getErrorMessageFromThrowable(throwable))
                        .build()
                );
    }

    public Callable<DependencyCheckResult> getCheckCallable() {
        return () -> {
            DependencyCheckResult.DependencyCheckResultBuilder builder = DependencyCheckResult.builder()
                    .type(getType())
                    .endpoint(getName())
                    .importance(getImportance())
                    .address(getAddress());

            Instant start = Instant.now();
            doCheck();
            Instant end = Instant.now();
            Long responseTime = Duration.between(start, end).toMillis();
            return builder.result(SelfCheckResult.OK).responseTime(String.valueOf(responseTime) + "ms").build();
        };
    }

    protected String getErrorMessageFromThrowable(Throwable e) {
        if (e instanceof TimeoutException) {
            return "Call to dependency timed out by circuitbreaker";
        }
        return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
    }

    protected String getErrorMessage(Exception e) {
        String message = e.getMessage().trim();
        String causeMessage = e.getCause() == null ? "" : ": " + e.getCause().getMessage();
        return message + causeMessage;
    }

}
