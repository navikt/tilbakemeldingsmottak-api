package no.nav.tilbakemeldingsmottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RegistrerTilbakemeldingRequest {

    private String erServiceklage;
    private String gjelder;
    private String paaklagetEnhet;
    private String behandlendeEnhet;
    private String ytelseTjeneste;
    private String tema;
    private String utfall;
    private ArrayList<String> svarmetode;

}