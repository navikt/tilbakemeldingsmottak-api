package no.nav.tilbakemeldingsmottak;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */

@ComponentScan(basePackages = "no.nav.tilbakemeldingsmottak")
@Configuration
@EnableAutoConfiguration
@EnableAspectJAutoProxy
public class CoreConfig {
}
