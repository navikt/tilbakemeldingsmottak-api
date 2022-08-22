package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@ConfigurationProperties("aad")
@Validated
public class AADProperties {
    @NotEmpty
    private String authority;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String serviceuser;
    @NotEmpty
    private String password;
}
