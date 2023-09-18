package no.nav.tilbakemeldingsmottak.metrics

import java.lang.annotation.Inherited

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@Inherited
annotation class Metrics(
    val value: String = "",
    val extraTags: Array<String> = [],
    val percentiles: DoubleArray = [],
    val description: String = "",
    val histogram: Boolean = false
)
