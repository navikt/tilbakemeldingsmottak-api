package no.nav.tilbakemeldingsmottak.consumer.aktoer.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Data
@Builder
public class AktoerResponse {

	Map<String, IdentInfoForAktoer> identer;
}
