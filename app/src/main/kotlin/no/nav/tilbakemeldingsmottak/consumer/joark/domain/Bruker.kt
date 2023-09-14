package no.nav.tilbakemeldingsmottak.consumer.joark.domain


data class Bruker(
    val idType: BrukerIdType, // Angir hvilken type identifikator som er benyttet i bruker.id
    val id: String // Brukerens fødselsnummer (11 siffer), aktørID (13 siffer) eller organisasjonsnummer (9 siffer)
)
