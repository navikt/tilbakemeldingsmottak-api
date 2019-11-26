package no.nav.tilbakemeldingsmottak.interceptors;

import static no.nav.tilbakemeldingsmottak.config.Constants.DOKLOGIN_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.REDIRECT_COOKIE;

import com.nimbusds.jwt.SignedJWT;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.OidcContextException;
import no.nav.tilbakemeldingsmottak.util.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LoginRedirectInterceptor extends AbstractInterceptor {

	private final OIDCRequestContextHolder oidcRequestContextHolder;
	private final String loginserviceUrl;

	LoginRedirectInterceptor(@Value("${loginservice.url}") String loginserviceUrl,
							 OIDCRequestContextHolder contextHolder) {
		this.oidcRequestContextHolder = contextHolder;
		this.loginserviceUrl = loginserviceUrl;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {
		TokenContext token = oidcRequestContextHolder.getOIDCValidationContext().getToken(DOKLOGIN_ISSUER);
		if (token == null) {
			response.addCookie(CookieUtils.createSessionCookie(REDIRECT_COOKIE, request.getRequestURI(), true));
			response.sendRedirect(loginserviceUrl);
			return false;
		}
		SignedJWT parsedConsumerToken = SignedJWT.parse(token.getIdToken());
		String consumerId = parsedConsumerToken.getJWTClaimsSet().getSubject();
		if (consumerId != null) {
			addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);
		} else {
			throw new OidcContextException("Kan ikke hente ut consumer-id fra token");
		}
		return true;
	}
}
