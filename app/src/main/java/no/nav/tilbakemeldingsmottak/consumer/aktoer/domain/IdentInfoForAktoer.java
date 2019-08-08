package no.nav.tilbakemeldingsmottak.consumer.aktoer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentInfoForAktoer {

	private List<IdentInfo> identer;
	private String feilmelding;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IdentInfo {
		private String ident;
		private String identgruppe;
		private Boolean gjeldende;
	}

	public String getFirstIdent() {
		return identer.iterator().next().getIdent();
	}
}
