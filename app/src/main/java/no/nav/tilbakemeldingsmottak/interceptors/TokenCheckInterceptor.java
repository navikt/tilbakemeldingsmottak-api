package no.nav.tilbakemeldingsmottak.interceptors;

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TokenCheckInterceptor extends AbstractInterceptor {

	private OIDCRequestContextHolder oidcRequestContextHolder;

	public TokenCheckInterceptor(OIDCRequestContextHolder contextHolder) {
		oidcRequestContextHolder = contextHolder;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		TokenContext consumerToken = oidcRequestContextHolder.getOIDCValidationContext().getToken("reststs");
		if (consumerToken == null) {
			String message = "Consumertoken må være satt";
			log.warn(message);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			return false;

		} else {
			SignedJWT parsedConsumerToken = SignedJWT.parse(consumerToken.getIdToken());
			String consumerId = parsedConsumerToken.getJWTClaimsSet().getSubject();
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
}
