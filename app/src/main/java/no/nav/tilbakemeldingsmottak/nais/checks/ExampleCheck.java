package no.nav.tilbakemeldingsmottak.nais.checks;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tilbakemeldingsmottak.model.DependencyCheckResult.ImportanceEnum;
import no.nav.tilbakemeldingsmottak.model.DependencyCheckResult.TypeEnum;
import no.nav.tilbakemeldingsmottak.nais.selftest.AbstractDependencyCheck;
import no.nav.tilbakemeldingsmottak.nais.selftest.ApplicationNotReadyException;
import org.springframework.stereotype.Component;
@Component
public class ExampleCheck extends AbstractDependencyCheck {


    public ExampleCheck(MeterRegistry registry) {
        super(TypeEnum.REST, "exampleConsumer", "exampleEndpoint", ImportanceEnum.WARNING, registry);
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
