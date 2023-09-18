package no.nav.tilbakemeldingsmottak.consumer.joark.domain

// Swagger: https://dokarkiv.dev.intern.nav.no/swagger-ui/index.html
data class AvsenderMottaker(
    //Identifikatoren til avsender/mottaker. Normalt et fødselsnummer eller organisasjonsnummer. Påkrevd dersom avsenderMottaker.idType er satt
    val id: String? = null,

    // Angir hvilken type identifikator som er benyttet i avsenderMottaker.id. Påkrevd dersom avsenderMottaker.id er satt.
    val idType: AvsenderMottakerIdType? = null,

    // Navnet til avsender/mottaker. Det er ikke nødvendig å oppgi navn når idType=FNR. OpprettJournalpost vil da hente personens navn fra PDL.
    val navn: String? = null,
)
