package no.nav.tilbakemeldingsmottak.consumer.joark.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DokumentInfo {
    private String dokumentInfoId;
    private String brevkode;
    private String tittel;
}
