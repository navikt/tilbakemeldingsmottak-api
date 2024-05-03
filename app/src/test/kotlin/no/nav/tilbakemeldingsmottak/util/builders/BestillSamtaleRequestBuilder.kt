package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleTidsrom
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleTidsrom.FORMIDDAG

class BestillSamtaleRequestBuilder {

    private var fornavn: String = "Test"
    private var etternavn: String = "Testesen"
    private var telefonnummer: String = "81549300"
    private var tidsrom: BestillSamtaleTidsrom? = FORMIDDAG

    fun build(
        fornavn: String = this.fornavn,
        etternavn: String = this.etternavn,
        telefonnummer: String = this.telefonnummer,
        tidsrom: BestillSamtaleTidsrom? = this.tidsrom
    ) = BestillSamtaleRequest(
        fornavn = fornavn,
        etternavn = etternavn,
        telefonnummer = telefonnummer,
        tidsrom = tidsrom
    )
}