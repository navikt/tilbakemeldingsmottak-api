package no.nav.tilbakemeldingsmottak.exceptions;

// En samling av alle error codes som frontend klienter kan bruke for å vise brukervennlige feilmeldinger
public enum ErrorCode {
    GENERAL_ERROR("GENERAL_ERROR"),
    AUTH_ERROR("AUTH_ERROR"),
    TOKEN_EMAIL_MISSING("TOKEN_EMAIL_MISSING"),

    // EREG
    EREG_UNAUTHORIZED("EREG_UNAUTHORIZED"), // mangler tilgang til å hente data fra ereg
    EREG_ERROR("EREG_ERROR"), // henting av organisasjonsdata feiler (feil/ugyldig orgnummer?)
    EREG_NOT_FOUND("EREG_NOT_FOUND"), // finner ikke organisasjon

    // DOKARKIV
    DOKARKIV_UNAUTHORIZED("DOKARKIV_UNAUTHORIZED"), // mangler tilgang til å opprette journalpost
    DOKARKIV_ERROR("DOKARKIV_ERROR"), // oppretting av journalpost feiler


    // NORG2
    NORG2_UNAUTHORIZED("NORG2_UNAUTHORIZED"), // mangler tilgang til å hente data fra norg2
    NORG2_ERROR("NORG2_ERROR"), // henting av enheter feiler

    // OPPGAVE
    OPPGAVE_UNAUTHORIZED("OPPGAVE_UNAUTHORIZED"), // mangler tilgang til å opprette/endre/hente oppgave
    OPPGAVE_ERROR("OPPGAVE_ERROR"), // oppretting/endring/henting av oppgave feiler
    OPPGAVE_COMPLETED("OPPGAVE_COMPLETED"), // oppgave er allerede ferdigstilt
    OPPGAVE_MISSING_JOURNALPOST("OPPGAVE_MISSING_JOURNALPOST"), // oppgave mangler journalpost
    OPPGAVE_NOT_FOUND("OPPGAVE_NOT_FOUND"), // finner ikke oppgave

    // PDL
    PDL_ERROR("PDL_ERROR"), // henting av persondata feiler (feil personnummer?)
    PDL_MISSING_AKTORID("PDL_MISSING_AKTORID"), // finner ingen aktørId for ident

    // SAF
    SAF_UNAUTHORIZED("SAF_UNAUTHORIZED"), // mangler tilgang til å hente data fra saf
    SAF_ERROR("SAF_ERROR"), // henting av dokument feiler
    SAF_NOT_FOUND("SAF_NOT_FOUND"), // finner ikke journalpost
    JOURNALPOST_MISSING_DOKUMENT("JOURNALPOST_MISSING_DOKUMENT"); // journalpost mangler dokument

    public final String value;

    ErrorCode(String value) {
        this.value = value;
    }
}
