package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;
import static org.apache.commons.lang3.StringUtils.isBlank;

import no.nav.tilbakemeldingsmottak.serviceklage.Klagetype;
import no.nav.tilbakemeldingsmottak.serviceklage.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpprettServiceklageRequestMapper {

    public Serviceklage map(OpprettServiceklageRequest request, boolean innlogget) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Serviceklage.builder()
                .opprettetDato(currentDateTime)
                .fremmetDato(currentDateTime.toLocalDate())
                .innsender(request.getPaaVegneAv().text)
                .klagenGjelderId(findKlagenGjelderId(request))
                .innlogget(innlogget)
                .klagetyper(mapKlagetype(request.getKlagetyper()))
                .klagetypeUtdypning(request.getKlagetyper().contains(Klagetype.ANNET) ? request.getKlagetypeUtdypning() : null)
                .gjelderSosialhjelp(request.getGjelderSosialhjelp() == null ? null : request.getGjelderSosialhjelp().text)
                .klagetekst(request.getKlagetekst())
                .svarmetode(mapSvarmetode(request.getOenskerAaKontaktes()))
                .svarmetodeUtdypning(mapSvarmetodeUtdypning(request.getOenskerAaKontaktes()))
                .kanal(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .enhetsnummerPaaklaget(!isBlank(request.getEnhetsnummerPaaklaget()) ? request.getEnhetsnummerPaaklaget() : null)
                .build();
    }

    private String mapKlagetype(List<Klagetype> klagetype) {
        return StringUtils.join(klagetype.stream().map(k -> k.text).collect(Collectors.toList()), ", ");
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
