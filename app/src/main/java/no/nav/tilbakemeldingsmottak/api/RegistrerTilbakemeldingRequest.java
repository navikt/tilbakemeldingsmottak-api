package no.nav.tilbakemeldingsmottak.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegistrerTilbakemeldingRequest {

    private String erServiceklage;
    private String gjelder;
    private String kanal;
    private String paaklagetEnhet;
    private String behandlendeEnhet;
    private String ytelseTjeneste;
    private String tema;
    private String utfall;
    private List<String> svarmetode;

}