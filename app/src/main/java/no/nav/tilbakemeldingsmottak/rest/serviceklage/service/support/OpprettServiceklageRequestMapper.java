package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;

import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpprettServiceklageRequestMapper {

    public Serviceklage map(OpprettServiceklageRequest request) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Serviceklage.builder()
                .opprettetDato(currentDateTime)
                .fremmetDato(currentDateTime)
                .innsender(request.getPaaVegneAv().text)
                .klagenGjelderId(findKlagenGjelderId(request))
                .klagetyper(mapKlagetype(request.getKlagetyper()))
                .klagetekst(request.getKlagetekst())
                .svarmetode(request.getOenskerAaKontaktes() != null && request.getOenskerAaKontaktes() ? null : SVAR_IKKE_NOEDVENDIG_ANSWER)
                .svarIkkeNoedvendig(request.getOenskerAaKontaktes() != null && request.getOenskerAaKontaktes() ? null : BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .kanal(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .build();
    }

    private String mapKlagetype(List<Klagetype> klagetype) {
        return StringUtils.join(klagetype.stream().map(k -> k.text).collect(Collectors.toList()), ", ");
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
