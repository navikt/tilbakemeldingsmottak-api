package no.nav.tilbakemeldingsmottak.serviceklage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HentDokumentResponse {
    byte[] dokument;
}
