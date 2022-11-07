package no.nav.tilbakemeldingsmottak.integration.sts;

import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
@Profile({"nais", "local"})
public class STSConfig {

	@Value("${securityTokenService_url}")
	private String stsUrl;
	private ServiceuserAlias serviceuserAlias;

	public STSConfig(ServiceuserAlias serviceuserAlias) {
		this.serviceuserAlias = serviceuserAlias;
	}

	public void configureSTS(Object port) {
		Client client = ClientProxy.getClient(port);
		STSConfigUtil.configureStsRequestSamlToken(client, stsUrl, serviceuserAlias.getUsername(), serviceuserAlias.getPassword());
	}

}
