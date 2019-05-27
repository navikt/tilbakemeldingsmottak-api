package no.nav.serviceklagemottak.nais;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import no.nav.serviceklagemottak.nais.selftest.AbstractDependencyCheck;
import no.nav.serviceklagemottak.nais.selftest.DependencyCheckResult;
import no.nav.serviceklagemottak.nais.selftest.Importance;
import no.nav.serviceklagemottak.nais.selftest.Result;
import no.nav.serviceklagemottak.nais.selftest.SelftestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class NaisContract {

	public static final String APPLICATION_ALIVE = "Application is alive!";
	public static final String APPLICATION_READY = "Application is ready for traffic!";
	private static final String APPLICATION_NOT_READY = "Application is not ready for traffic :-(";

	private final String appName;
	private final String version;
	private final List<AbstractDependencyCheck> dependencyCheckList;

	private AtomicInteger app_status = new AtomicInteger();

	@Inject
	public NaisContract(List<AbstractDependencyCheck> dependencyCheckList, MeterRegistry registry, @Value("${APP_NAME:serviceklagemottak}") String appName, @Value("${APP_VERSION:0}") String version) {
		this.dependencyCheckList = new ArrayList<>(dependencyCheckList);
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
		List<DependencyCheckResult> results = new ArrayList<>();

		checkCriticalDependencies(results);

		if (isAnyVitalDependencyUnhealthy(results.stream()
				.map(DependencyCheckResult::getResult)
				.collect(Collectors.toList()))) {
			app_status.set(0);
			return new ResponseEntity<>(APPLICATION_NOT_READY, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		app_status.set(1);

		return new ResponseEntity<>(APPLICATION_READY, HttpStatus.OK);
	}

	@GetMapping("/internal/selftest")
	public @ResponseBody
	SelftestResult selftest() {
		List<DependencyCheckResult> results = new ArrayList<>();
		checkDependencies(results);
		return SelftestResult.builder()
				.appName(appName)
				.version(version)
				.dependencyCheckResults(results)
				.result(getOverallSelftestResult(results))
				.build();
	}

	private boolean isAnyVitalDependencyUnhealthy(List<Result> results) {
		return results.stream().anyMatch((result) -> result.equals(Result.ERROR));
	}


	private Result getOverallSelftestResult(List<DependencyCheckResult> results) {
		if (results.stream().anyMatch((result) -> result.getResult().equals(Result.ERROR))) {
			return Result.ERROR;
		} else if (results.stream().anyMatch((result) -> result.getResult().equals(Result.WARNING))) {
			return Result.WARNING;
		}

		return Result.OK;
	}


	private void checkCriticalDependencies(List<DependencyCheckResult> results) {

		Flowable.fromIterable(dependencyCheckList)
				.filter(dependency -> dependency.getImportance().equals(Importance.CRITICAL))
				.parallel()
				.runOn(Schedulers.io())
				.map(payload -> payload.check().get())
				.sequential().blockingSubscribe(results::add);
	}

	private void checkDependencies(List<DependencyCheckResult> results) {

		Flowable.fromIterable(dependencyCheckList)
				.parallel()
				.runOn(Schedulers.io())
				.map(payload -> payload.check().get())
				.sequential().blockingSubscribe(results::add);
	}

}