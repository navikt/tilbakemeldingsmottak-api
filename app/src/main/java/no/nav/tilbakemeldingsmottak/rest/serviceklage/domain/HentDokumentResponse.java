package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HentDokumentResponse {
    byte[] dokument;
}
