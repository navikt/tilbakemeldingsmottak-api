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

		if (oidcRequestContextHolder.getOIDCValidationContext().hasValidToken() &&
				oidcRequestContextHolder.getOIDCValidationContext().getFirstValidToken().isPresent()) {
			TokenContext tokenContext = oidcRequestContextHolder.getOIDCValidationContext().getFirstValidToken().get();
			SignedJWT parsedToken = SignedJWT.parse(tokenContext.getIdToken());
			consumerId = parsedToken.getJWTClaimsSet().getSubject();
			if (consumerId != null && consumerId.startsWith("srv")) {
				addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);
			}  else {
				String message = "OIDC token på Authorization header må tilhøre en Servicebruker";
				log.warn(message);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				return false;
			}
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
