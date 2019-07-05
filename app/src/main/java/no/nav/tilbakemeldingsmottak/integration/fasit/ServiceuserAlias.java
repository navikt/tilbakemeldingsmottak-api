package no.nav.tilbakemeldingsmottak.integration.fasit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author Joakim Bjørnstad, Jbit AS
 */
@Getter
@Setter
@ToString
@ConfigurationProperties("serviceuser")
@Validated
public class ServiceuserAlias {
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
}
