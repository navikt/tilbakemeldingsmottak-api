package no.nav.tilbakemeldingsmottak.consumer.joark.domain

data class Dokument(
    // Dokumentets tittel, f.eks. 'Søknad om foreldrepenger ved fødsel' eller 'Legeerklæring'. Dokumentets tittel blir synlig i brukers journal på nav.no, samt i NAVs fagsystemer.
    val tittel: String,

    // Kode som sier noe om dokumentets innhold og oppbygning.
    //For inngående dokumenter kan brevkoden være en NAV-skjemaID f.eks. 'NAV 14-05.09' eller en SED-id.
    //Utgående dokumenter og notater bør ha brevkode, og verdien bestemmes av konsument. Bruk gjerne brevets intern kode i fagsystemet. Brevkode skal ikke settes for ustrukturert, uklassifisert dokumentasjon, f.eks. brukeropplastede vedlegg.
    val brevkode: String? = null,

    // Alle variantene av et enkeltdokument som skal arkiveres
    val dokumentvarianter: List<DokumentVariant> = ArrayList()
)
