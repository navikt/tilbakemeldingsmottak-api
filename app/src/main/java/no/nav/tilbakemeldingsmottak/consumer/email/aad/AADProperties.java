package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "aad")
@Validated
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
