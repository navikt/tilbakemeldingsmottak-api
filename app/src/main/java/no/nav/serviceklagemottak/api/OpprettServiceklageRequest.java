package no.nav.serviceklagemottak.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OpprettServiceklageRequest {

    private String email;
    private String klagetekst;

}
