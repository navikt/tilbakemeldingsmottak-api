package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import static no.nav.tilbakemeldingsmottak.config.Constants.REDIRECT_COOKIE;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.util.CookieUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Slf4j
@Controller
public class LoginController {

	@Unprotected
	@RequestMapping("serviceklage/**")
	public String forwardToRoot() {
		return "forward:/";
	}

	@Unprotected
	@RequestMapping("/login")
	public String redirect(HttpServletRequest request, HttpServletResponse response) {
		Optional<Cookie> redirectCookie = CookieUtils.findCookie(request.getCookies(), REDIRECT_COOKIE);
		if (redirectCookie.isPresent()) {
			response.addCookie(CookieUtils.createSessionClearingCookie(REDIRECT_COOKIE, true));
		}
		return "redirect:" + redirectCookie
				.map(Cookie::getValue)
				.filter(cookie -> !"/login".equals(cookie) && !StringUtils.isEmpty(cookie))
				.orElse("/serviceklage/frontpage");
	}
}