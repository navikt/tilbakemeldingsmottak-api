package no.nav.tilbakemeldingsmottak.consumer.norg2;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Enhet {

	private String navn;
	private String enhetNr;
	private String status;

}
