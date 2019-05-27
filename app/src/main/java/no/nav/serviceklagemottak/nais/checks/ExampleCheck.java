package no.nav.serviceklagemottak.nais.checks;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.serviceklagemottak.nais.selftest.AbstractDependencyCheck;
import no.nav.serviceklagemottak.nais.selftest.ApplicationNotReadyException;
import no.nav.serviceklagemottak.nais.selftest.DependencyType;
import no.nav.serviceklagemottak.nais.selftest.Importance;
import org.springframework.stereotype.Component;
@Component
public class ExampleCheck extends AbstractDependencyCheck {


    public ExampleCheck(MeterRegistry registry) {
        super(DependencyType.REST, "exampleConsumer", "exampleEndpoint", Importance.WARNING, registry);
    }

    @Override
    protected void doCheck() {
        try {
//            consumer.ping();
        } catch (Exception e) {
            throw new ApplicationNotReadyException(String.format("Calling [name] failed. errorMessage=%s", getErrorMessage(e)), e);
        }
    }


}
