package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.AARSAK;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BESKRIVELSE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.GJELDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INFORMASJON;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNSENDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.PAAKLAGET_ENHET_ER_BEHANDLENDE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TEMA;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TEMA_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TILGJENGELIGHET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TILTAK;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.UTFALL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.VEILEDNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.VENTE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.YTELSE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KlassifiserServiceklageRequest {

    @JsonProperty(BEHANDLES_SOM_SERVICEKLAGE)
    private String behandlesSomServiceklage;

    @JsonProperty(BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING)
    private String behandlesSomServiceklageUtdypning;

    @JsonProperty(FREMMET_DATO)
    private String fremmetDato;

    @JsonProperty(INNSENDER)
    private String innsender;

    @JsonProperty(KANAL)
    private String kanal;

    @JsonProperty(KANAL_UTDYPNING)
    private String kanalUtdypning;

    @JsonProperty(PAAKLAGET_ENHET_ER_BEHANDLENDE)
    private String paaklagetEnhetErBehandlende;

    @JsonProperty(ENHETSNUMMER_PAAKLAGET)
    private String enhetsnummerPaaklaget;

    @JsonProperty(ENHETSNUMMER_BEHANDLENDE)
    private String enhetsnummerBehandlende;

    @JsonProperty(GJELDER)
    private String gjelder;

    @JsonProperty(BESKRIVELSE)
    private String beskrivelse;

    @JsonProperty(YTELSE)
    private String ytelse;

    @JsonProperty(TEMA)
    private String tema;

    @JsonProperty(VENTE)
    private String vente;

    @JsonProperty(TILGJENGELIGHET)
    private String tilgjengelighet;

    @JsonProperty(INFORMASJON)
    private String informasjon;

    @JsonProperty(VEILEDNING)
    private String veiledning;

    @JsonProperty(TEMA_UTDYPNING)
    private String temaUtdypning;

    @JsonProperty(UTFALL)
    private String utfall;

    @JsonProperty(AARSAK)
    private String aarsak;

    @JsonProperty(TILTAK)
    private String tiltak;

    @JsonProperty(SVARMETODE)
    private String svarmetode;

    @JsonProperty(SVARMETODE_UTDYPNING)
    private String svarmetodeUtdypning;

    @JsonProperty(SVAR_IKKE_NOEDVENDIG)
    private String svarIkkeNoedvendig;

}