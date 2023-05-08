package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class OpprettServiceklageRequestMapper {

    public Serviceklage map(OpprettServiceklageRequest request, boolean innlogget) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Serviceklage.builder()
                .opprettetDato(currentDateTime)
                .fremmetDato(currentDateTime.toLocalDate())
                .innsender(request.getPaaVegneAv().value)
                .klagenGjelderId(findKlagenGjelderId(request))
                .innlogget(innlogget)
                .klagetyper(mapKlagetype(request.getKlagetyper()))
                .klagetypeUtdypning(request.getKlagetyper().contains(KlagetyperEnum.ANNET) ? request.getKlagetypeUtdypning() : null)
                .gjelderSosialhjelp(request.getGjelderSosialhjelp() == null ? null : request.getGjelderSosialhjelp().value)
                .klagetekst(request.getKlagetekst())
                .svarmetode(mapSvarmetode(request.getOenskerAaKontaktes()))
                .svarmetodeUtdypning(mapSvarmetodeUtdypning(request.getOenskerAaKontaktes()))
                .kanal(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .enhetsnummerPaaklaget(!isBlank(request.getEnhetsnummerPaaklaget()) ? request.getEnhetsnummerPaaklaget() : null)
                .build();
    }

    private String mapKlagetype(List<KlagetyperEnum> klagetype) {
        return StringUtils.join(klagetype.stream().map(x -> x.value).collect(Collectors.toList()), ", ");
    }

    private String mapSvarmetode(Boolean oenskerAaKontaktes) {
        if (oenskerAaKontaktes == null) {
            return SVAR_IKKE_NOEDVENDIG_ANSWER;
        } else {
            return oenskerAaKontaktes ? null : SVAR_IKKE_NOEDVENDIG_ANSWER;
        }
    }

    private String mapSvarmetodeUtdypning(Boolean oenskerAaKontaktes) {
        if (oenskerAaKontaktes == null) {
            return INNMELDER_MANGLER_FULLMAKT_ANSWER;
        } else {
            return oenskerAaKontaktes ? null : BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
        }
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
