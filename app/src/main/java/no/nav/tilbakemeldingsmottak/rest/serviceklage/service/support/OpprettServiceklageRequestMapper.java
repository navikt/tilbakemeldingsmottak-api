package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;

import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OpprettServiceklageRequestMapper {

    public Serviceklage map(OpprettServiceklageRequest request) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Serviceklage.builder()
                .opprettetDato(currentDateTime)
                .fremmetDato(currentDateTime)
                .innsender(request.getPaaVegneAv().text)
                .klagenGjelderId(findKlagenGjelderId(request))
                .klagetype(request.getKlagetype().text)
                .klagetekst(request.getKlagetekst())
                .svarmetode(request.getOenskerAaKontaktes() ? null : SVAR_IKKE_NOEDVENDIG_ANSWER)
                .svarIkkeNoedvendig(request.getOenskerAaKontaktes() ? null : BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .kanal(KANAL_SERVICEKLAGESKJEMA_ANSWER)
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
