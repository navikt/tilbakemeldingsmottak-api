package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HentServiceklageResponse {
    Serviceklage serviceklage;
    byte[] dokument;
}
