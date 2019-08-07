package no.nav.tilbakemeldingsmottak.consumer.sts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class STSResponse {
	private final String accessToken;
	private final String tokenType;
	private final String expiresIn;

	@JsonCreator
	public STSResponse(@JsonProperty("access_token") String accessToken, @JsonProperty("token_type") String tokenType, @JsonProperty("expires_in") String expiresIn) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
	}
}
