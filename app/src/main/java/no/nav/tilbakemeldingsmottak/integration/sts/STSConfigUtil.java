package no.nav.tilbakemeldingsmottak.integration.sts;

import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;

import java.util.HashMap;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
final class STSConfigUtil {

	private static final String STS_REQUEST_SAML_POLICY = "classpath:policy/requestSamlPolicy.xml";
	private static final String STS_CLIENT_AUTHENTICATION_POLICY = "classpath:policy/untPolicy.xml";

	private STSConfigUtil() {
	}

	static void configureStsRequestSamlToken(Client client, String stsUrl, String username, String password) {
		STSClient stsClient = new STSClient(client.getBus());
		configureSTSClient(stsClient, stsUrl, username, password);

		client.getRequestContext().put(SecurityConstants.STS_CLIENT, stsClient);
		//Using CXF cache
		client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);

		setClientEndpointPolicy(client, resolvePolicyReference(client, STS_REQUEST_SAML_POLICY));
	}

	private static void configureSTSClient(STSClient stsClient, String location, String username, String password) {

		stsClient.setEnableAppliesTo(false);
		stsClient.setAllowRenewing(false);
		stsClient.setLocation(location);

		HashMap<String, Object> properties = new HashMap<>();
		properties.put(SecurityConstants.USERNAME, username);
		properties.put(SecurityConstants.PASSWORD, password);

		stsClient.setProperties(properties);

		//used for the STS client to authenticate itself to the STS provider.
		stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
	}

	private static Policy resolvePolicyReference(Client client, String uri) {
		PolicyBuilder policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
		ReferenceResolver resolver = new RemoteReferenceResolver("", policyBuilder);
		return resolver.resolveReference(uri);
	}

	private static void setClientEndpointPolicy(Client client, Policy policy) {
		Endpoint endpoint = client.getEndpoint();
		EndpointInfo endpointInfo = endpoint.getEndpointInfo();

		PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
		SoapMessage message = new SoapMessage(Soap12.getInstance());
		EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
		policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
	}
}
