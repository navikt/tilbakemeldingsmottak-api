package no.nav.tilbakemeldingsmottak.interceptors;

import static no.nav.tilbakemeldingsmottak.config.Constants.LOGINSERVICE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.REDIRECT_COOKIE;

import com.nimbusds.jwt.SignedJWT;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.OidcContextException;
import no.nav.tilbakemeldingsmottak.util.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LoginRedirectInterceptor extends AbstractInterceptor {

	private final TokenValidationContextHolder tokenValidationContextHolder;
	private final String loginserviceUrl;

	LoginRedirectInterceptor(@Value("${loginservice.url}") String loginserviceUrl,
							 TokenValidationContextHolder contextHolder) {
		this.tokenValidationContextHolder = contextHolder;
		this.loginserviceUrl = loginserviceUrl;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		JwtToken token = tokenValidationContextHolder.getTokenValidationContext().getJwtToken(LOGINSERVICE_ISSUER);
		if (token == null) {
		    String redirect = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request)).build().toUriString();
            response.addCookie(CookieUtils.createSessionCookie(REDIRECT_COOKIE, redirect, true));
			response.sendRedirect(loginserviceUrl);
			return false;
		}
		SignedJWT parsedConsumerToken = SignedJWT.parse(token.getSubject());
		String consumerId = parsedConsumerToken.getJWTClaimsSet().getSubject();
		if (consumerId != null) {
			addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);
		} else {
			throw new OidcContextException("Kan ikke hente ut consumer-id fra token");
		}
		return true;
	}
}
