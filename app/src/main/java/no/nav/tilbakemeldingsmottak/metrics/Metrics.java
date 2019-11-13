package no.nav.tilbakemeldingsmottak.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Metrics{
	String value() default "";

	String[] extraTags() default {};

	double[] percentiles() default {};

	String description() default "";

	boolean histogram() default false;

}
