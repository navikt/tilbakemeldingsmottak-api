package no.nav.serviceklagemottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Innmelder {

    private String navn;
    private String personnummer;
    private String telefonnummer;
    private boolean harFullmakt;
    private String rolle;

}
