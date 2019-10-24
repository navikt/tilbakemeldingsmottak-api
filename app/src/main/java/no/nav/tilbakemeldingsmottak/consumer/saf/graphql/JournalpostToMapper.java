package no.nav.tilbakemeldingsmottak.consumer.saf.graphql;

import static no.nav.tilbakemeldingsmottak.consumer.saf.util.MappingUtil.stringToEnum;

import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Journalpost;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat;

import java.util.List;
import java.util.stream.Collectors;

public class JournalpostToMapper {

	public Journalpost map(SafJournalpostTo safJournalpostTo) {
		return Journalpost.builder()
				.dokumenter(mapDokumenter(safJournalpostTo.getDokumenter()))
				.build();
	}

	private List<Journalpost.DokumentInfo> mapDokumenter(List<SafJournalpostTo.DokumentInfo> dokumenter) {
		return dokumenter
				.stream()
				.map(this::mapDokument)
				.collect(Collectors.toList());
	}

	private Journalpost.DokumentInfo mapDokument(SafJournalpostTo.DokumentInfo dokumentInfo) {
		return Journalpost.DokumentInfo.builder()
				.dokumentInfoId(dokumentInfo.getDokumentInfoId())
				.dokumentvarianter(mapDokumentVarianter(dokumentInfo.getDokumentvarianter()))
				.build();
	}

	private List<Journalpost.Dokumentvariant> mapDokumentVarianter(List<SafJournalpostTo.Dokumentvariant> dokumentvarianter) {
		return dokumentvarianter
				.stream()
				.filter(this::isVariantformatArkivOrSladdet)
				.map(this::mapDokumentVariant)
				.collect(Collectors.toList());

	}

	private Journalpost.Dokumentvariant mapDokumentVariant(SafJournalpostTo.Dokumentvariant dokumentvariant) {
		return Journalpost.Dokumentvariant.builder()
				.variantformat(stringToEnum(Variantformat.class, dokumentvariant.getVariantformat()))
				.saksbehandlerHarTilgang(dokumentvariant.isSaksbehandlerHarTilgang())
				.build();
	}

	private boolean isVariantformatArkivOrSladdet(SafJournalpostTo.Dokumentvariant dokumentvariant) {
		return Variantformat.ARKIV.name().equals(dokumentvariant.getVariantformat()) || Variantformat.SLADDET.name().equals(dokumentvariant.getVariantformat());
	}
}
