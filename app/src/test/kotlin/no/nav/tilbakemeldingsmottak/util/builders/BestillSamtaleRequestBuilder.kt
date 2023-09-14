package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest.Tidsrom

class BestillSamtaleRequestBuilder {

    private var fornavn: String = "Test"
    private var etternavn: String = "Testesen"
    private var telefonnummer: String = "81549300"
    private var tidsrom: Tidsrom? = Tidsrom.FORMIDDAG

    fun build(
        fornavn: String = this.fornavn,
        etternavn: String = this.etternavn,
        telefonnummer: String = this.telefonnummer,
        tidsrom: Tidsrom? = this.tidsrom
    ) = BestillSamtaleRequest(
        fornavn = fornavn,
        etternavn = etternavn,
        telefonnummer = telefonnummer,
        tidsrom = tidsrom
    )
}