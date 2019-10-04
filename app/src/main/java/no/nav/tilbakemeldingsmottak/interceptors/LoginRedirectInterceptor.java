package no.nav.tilbakemeldingsmottak.interceptors;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static no.nav.tilbakemeldingsmottak.config.Constants.LOGINSERVICE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.REDIRECT_COOKIE;

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
		if (oidcRequestContextHolder.getOIDCValidationContext().getToken(LOGINSERVICE_ISSUER) == null) {
			Cookie cookie = new Cookie(REDIRECT_COOKIE, request.getRequestURI());
			cookie.setPath("/");
			cookie.setMaxAge(-1);
			cookie.setHttpOnly(true);
			response.addCookie(cookie);
			response.sendRedirect(loginserviceUrl);
			return false;
		}
		return true;
	}
}
