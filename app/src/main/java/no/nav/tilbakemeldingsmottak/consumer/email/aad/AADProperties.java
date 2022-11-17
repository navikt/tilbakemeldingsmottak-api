package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ConfigurationProperties(prefix = "aad")
@Validated
@Profile("nais | spring")
public class AADProperties {
    @NotEmpty
    private String authority;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String serviceuser;
    @NotEmpty
    private String password;

    private String proxyHost;
    private Integer proxyPort;
    private String tenant;

}
