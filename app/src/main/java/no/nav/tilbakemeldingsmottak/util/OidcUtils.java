package no.nav.tilbakemeldingsmottak.util;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tilbakemeldingsmottak.exceptions.OidcContextException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OidcUtils {
    private final OIDCRequestContextHolder oidcRequestContextHolder;

    public Optional<String> getSubjectForIssuer(String issuer) {
        TokenContext userToken = oidcRequestContextHolder.getOIDCValidationContext().getToken(issuer);
        if (userToken == null) {
            return Optional.empty();
        } else {
            try {
                SignedJWT parsedUserToken = SignedJWT.parse(userToken.getIdToken());
                return Optional.of(parsedUserToken.getJWTClaimsSet().getSubject());
            } catch (ParseException e) {
                throw new OidcContextException("Feil i parsing av token");
            }
        }
    }

    public Optional<String> getEmailForIssuer(String issuer) {
        TokenContext userToken = oidcRequestContextHolder.getOIDCValidationContext().getToken(issuer);
        if (userToken == null) {
            return Optional.empty();
        } else {
            try {
                SignedJWT parsedUserToken = SignedJWT.parse(userToken.getIdToken());
                return Optional.of(parsedUserToken.getPayload().toJSONObject().getAsString("upn"));
            } catch (ParseException e) {
                throw new OidcContextException("Feil i parsing av token");
            }
        }
    }

    public String getFirstValidToken() {
        return oidcRequestContextHolder.getOIDCValidationContext().getFirstValidToken()
                .map(TokenContext::getIdToken)
                .orElseThrow(() -> new OidcContextException("Finner ikke validert OIDC-token"));
    }

}
