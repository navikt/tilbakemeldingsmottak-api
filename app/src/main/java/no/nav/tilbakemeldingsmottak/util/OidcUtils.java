package no.nav.tilbakemeldingsmottak.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OidcUtils {
    private final TokenValidationContextHolder tokenValidationContextHolder;

    public Optional<String> getSubjectForIssuer(String issuer) {
        JwtToken userToken = tokenValidationContextHolder.getTokenValidationContext().getJwtToken(issuer);
        if (userToken == null) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(userToken.getSubject());
            } catch (Exception e) {
                throw new RuntimeException("Feil i parsing av token",e);
            }
        }
    }

    public Optional<String> getEmailForIssuer(String issuer) {
        JwtToken userToken = tokenValidationContextHolder.getTokenValidationContext().getJwtToken(issuer);
        if (userToken == null) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(userToken.getJwtTokenClaims().getStringClaim("upn"));
            } catch (Exception e) {
                throw new RuntimeException("Feil i parsing av token",e);
            }
        }
    }

    public String getFirstValidToken() {
        return tokenValidationContextHolder.getTokenValidationContext().getFirstValidToken().map(JwtToken::getTokenAsString)
                .orElseThrow(() -> new RuntimeException("Finner ikke validert OIDC-token"));
    }

    public Optional<String> getSubjectForFirstValidToken() {
        Optional<JwtToken> userToken =  tokenValidationContextHolder.getTokenValidationContext().getFirstValidToken();
        if (!userToken.isPresent()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(userToken.get().getSubject());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    public void checkSubjectOnEachIssuer() {
        List<String> issuers = tokenValidationContextHolder.getTokenValidationContext().getIssuers();
        issuers.stream()
                .forEach(e -> logClaims(e));
    }


    private void logClaims(String issuer) {
        Optional<JwtToken> userToken = tokenValidationContextHolder.getTokenValidationContext().getJwtTokenAsOptional(issuer);
        log.info("Context issuer=" + issuer + " User="+ (userToken.isPresent()? userToken.get().getSubject(): "Ikke funnet"));
    }
}
