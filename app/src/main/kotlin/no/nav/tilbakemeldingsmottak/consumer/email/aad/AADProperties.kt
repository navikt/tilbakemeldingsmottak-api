package no.nav.tilbakemeldingsmottak.consumer.email.aad

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "aad")
@Validated
class AADProperties {
    @NotEmpty
    var authority: String = ""

    @NotEmpty
    var clientId: String = ""

    @NotEmpty
    var clientSecret: String = ""

    @NotEmpty
    var email: String = ""

    var tenant: String? = null
}
