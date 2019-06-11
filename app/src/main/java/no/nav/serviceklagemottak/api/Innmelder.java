package no.nav.serviceklagemottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Innmelder {
    private String navn;
    private String personnummer;
    private String telefonnummer;
    private Boolean harFullmakt;
    private String rolle;
}
