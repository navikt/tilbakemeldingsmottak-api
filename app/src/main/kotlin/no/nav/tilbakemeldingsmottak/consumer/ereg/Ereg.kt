package no.nav.tilbakemeldingsmottak.consumer.ereg

interface Ereg {
    fun hentInfo(orgnr: String): String
}
