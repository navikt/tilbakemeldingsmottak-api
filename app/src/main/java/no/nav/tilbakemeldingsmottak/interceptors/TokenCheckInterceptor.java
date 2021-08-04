package no.nav.tilbakemeldingsmottak.interceptors;

import static no.nav.tilbakemeldingsmottak.config.Constants.RESTSTS_ISSUER;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TokenCheckInterceptor extends AbstractInterceptor {

	private final TokenValidationContextHolder tokenValidationContextHolder;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		JwtToken consumerToken = tokenValidationContextHolder.getTokenValidationContext().getJwtToken(RESTSTS_ISSUER);
		if (consumerToken == null) {
			String message = "Consumertoken må være satt";
			log.warn(message);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			return false;

		} else {
			String consumerId = consumerToken.getSubject();
			if (consumerId != null && consumerId.startsWith("srv")) {
				addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);
			}  else {
				String message = "Consumertoken må tilhøre en servicebruker";
				log.warn(message);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				return false;
			}
		}
		return true;
	}
}
