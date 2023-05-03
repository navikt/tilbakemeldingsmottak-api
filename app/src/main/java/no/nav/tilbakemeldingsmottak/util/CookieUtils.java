package no.nav.tilbakemeldingsmottak.util;

import org.springframework.lang.NonNull;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

public final class CookieUtils {
    private CookieUtils() {
    }

    public static Optional<Cookie> findCookie(Cookie[] cookies, @NonNull String name) {
        return Arrays.stream(Optional.ofNullable(cookies).orElse(new Cookie[]{}))
                .filter(cookie -> name.equals(cookie.getName()))
                .findAny();
    }

    public static Cookie createSessionClearingCookie(String name, boolean httpOnly) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(0);
        return cookie;
    }

    public static Cookie createSessionCookie(String name, String value, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(-1);
        return cookie;
    }
}
