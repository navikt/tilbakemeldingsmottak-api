package no.nav.tilbakemeldingsmottak.interceptors;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class MDCPopulationInterceptor extends HandlerInterceptorAdapter {

	private OIDCRequestContextHolder oidcRequestContextHolder;

	public MDCPopulationInterceptor(OIDCRequestContextHolder contextHolder) {
		oidcRequestContextHolder = contextHolder;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String callId = getHeaderValuesFromRequest(request, UUID.randomUUID().toString(),
				"nav-callid", "nav-call-id", "x_callId", "callid");
		addValueToMDC(callId, MDCConstants.MDC_CALL_ID);

		String consumerId = getHeaderValuesFromRequest(request, "tilbakemeldingsmottak",
				"nav-consumerid", "nav-consumer-id", "x_consumerid", "consumerid");
		addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);

		TokenContext consumerToken = oidcRequestContextHolder.getOIDCValidationContext().getToken("reststs");
		if (consumerToken == null) {
			String message = "Consumertoken må være satt";
			log.warn(message);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			return false;

		} else {
			SignedJWT parsedConsumerToken = SignedJWT.parse(consumerToken.getIdToken());
			consumerId = parsedConsumerToken.getJWTClaimsSet().getSubject();
			if (consumerId != null && consumerId.startsWith("srv")) {
				addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);
			}  else {
				String message = "Consumertoken må tilhøre en servicebruker";
				log.warn(message);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				return false;
			}
		}

		TokenContext userToken = oidcRequestContextHolder.getOIDCValidationContext().getToken("azuread");
		if (userToken != null) {
			SignedJWT parsedUserToken = SignedJWT.parse(userToken.getIdToken());
			String userId = parsedUserToken.getJWTClaimsSet().getSubject();
			addValueToMDC(userId, MDCConstants.MDC_USER_ID);
		}

		return true;
	}

	/**
	 * @return Return the value in the first header that is defined in the request
	 */
	private String getHeaderValuesFromRequest(HttpServletRequest request, String fallbackValue, String... headerNames) {
		for (String headerName : headerNames) {
			String value = request.getHeader(headerName);
			if (!isBlank(value)) {
				return value;
			}
		}
		return fallbackValue;
	}

	private boolean addValueToMDC(String value, String key) {
		if (value != null && !value.isEmpty()) {
			MDC.put(key, value);
			return true;
		}
		return false;
	}
}
