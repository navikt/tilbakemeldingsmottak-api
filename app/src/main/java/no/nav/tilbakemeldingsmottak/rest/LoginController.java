package no.nav.tilbakemeldingsmottak.rest;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.util.CookieUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import static no.nav.tilbakemeldingsmottak.config.Constants.LOGINSERVICE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.REDIRECT_COOKIE;

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
			try {
				URI uri = new URI(redirectCookie.get().getValue());
				if (uri.isAbsolute()) {
					throw new URISyntaxException(uri.toString(), "Value of redirect cookie is an absolute url");
				}
			} catch (URISyntaxException e) {
				log.warn("Invalid redirect cookie", e);
				redirectCookie = Optional.empty();
			} finally {
				response.addCookie(CookieUtils.createSessionClearingCookie(REDIRECT_COOKIE, true));
			}
		}
		return "redirect:" + redirectCookie
				.map(Cookie::getValue)
				.filter(cookie -> !"/login".equals(cookie) && !StringUtils.isEmpty(cookie))
				.orElse("/serviceklage/frontpage");
	}

	@Unprotected
	@Profile("local")
	@GetMapping(value = "/login/local", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> getCookieForm(
			@Nullable @RequestParam("token") String token, HttpServletResponse response) throws ParseException {

		if (token == null) {
			return ResponseEntity.ok("<form><input name=\"token\" type=\"text\"><input type=\"submit\"></form>");
		}
		SignedJWT.parse(token);

		response.addCookie(CookieUtils.createSessionCookie(LOGINSERVICE_ISSUER + "-idtoken", token, true));
		return ResponseEntity.status(HttpStatus.FOUND)
				.header("location", "/login")
				.build();
	}
}