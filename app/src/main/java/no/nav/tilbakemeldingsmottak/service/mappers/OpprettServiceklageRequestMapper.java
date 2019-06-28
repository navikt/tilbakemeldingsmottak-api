package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OpprettServiceklageRequestMapper {

    public Serviceklage map(OpprettServiceklageRequest request) {
        return Serviceklage.builder()
                .datoOpprettet(LocalDateTime.now())
                .paaVegneAv(request.getPaaVegneAv().name())
                .klagenGjelderId(findKlagenGjelderId(request))
                .klagetype(request.getKlagetype())
                .klagetekst(request.getKlagetekst())
                .oenskerAaKontaktes(request.getOenskerAaKontaktes())
                .build();
    }

    private String findKlagenGjelderId(OpprettServiceklageRequest request) {
        String klagenGjelderId = null;
        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                klagenGjelderId = request.getInnmelder().getPersonnummer();
                break;
            case ANNEN_PERSON:
                klagenGjelderId = request.getPaaVegneAvPerson().getPersonnummer();
                break;
            case BEDRIFT:
                klagenGjelderId = request.getPaaVegneAvBedrift().getOrganisasjonsnummer();
                break;
        }
        return klagenGjelderId;
    }

}
