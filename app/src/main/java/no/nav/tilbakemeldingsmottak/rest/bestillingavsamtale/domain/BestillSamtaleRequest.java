package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BestillSamtaleRequest {
    private String fornavn;
    private String etternavn;
    private String telefonnummer;
    private Tidsrom tidsrom;
}
