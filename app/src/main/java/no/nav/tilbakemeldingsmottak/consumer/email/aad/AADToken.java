package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AADToken {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime expires;

    public AADToken(String accessToken, String refreshToken, LocalDateTime expires) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expires = expires;
    }

    public AADToken(String accessToken, LocalDateTime expires) {
        this.accessToken = accessToken;
        this.expires = expires;
    }
}
