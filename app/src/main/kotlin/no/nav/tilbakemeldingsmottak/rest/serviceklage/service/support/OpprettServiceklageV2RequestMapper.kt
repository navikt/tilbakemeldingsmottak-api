package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageV2Request
import org.springframework.stereotype.Component

@Component
class OpprettServiceklageV2RequestMapper {
    fun map(request: OpprettServiceklageV2Request): OpprettServiceklageRequest {
        return OpprettServiceklageRequest(
            paaVegneAv = request.paaVegneAv,
            innmelder = request.innmelder,
            paaVegneAvPerson = request.paaVegneAvPerson,
            paaVegneAvBedrift = request.paaVegneAvBedrift,
            enhetsnummerPaaklaget = request.enhetsnummerPaaklaget,
            klagetyper = null,
            klagetypeUtdypning = null,
            gjelderSosialhjelp = null,
            klagetekst = request.klagetekst,
            oenskerAaKontaktes = request.oenskerAaKontaktes
        )
    }
}
