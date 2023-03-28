package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ConfigurationProperties(prefix = "aad")
@Validated
@Profile("nais | local")
public class AADProperties {
    @NotEmpty
    private String authority;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;
    @NotEmpty
    private String email;
    private String tenant;

}
