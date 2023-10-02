package no.nav.tilbakemeldingsmottak.exceptions

// En samling av alle error codes som frontend klienter kan bruke for å vise brukervennlige feilmeldinger
enum class ErrorCode(val value: String) {
    // GENERELLE
    GENERAL_ERROR("GENERAL_ERROR"), // generell feilmelding "noe gikk galt"
    AUTH_ERROR("AUTH_ERROR"), // feilmelding for autentisering
    TOKEN_EMAIL_MISSING("TOKEN_EMAIL_MISSING"), // kan ikke hente ut epost fra token

    // EREG
    EREG_UNAUTHORIZED("EREG_UNAUTHORIZED"), // ikke autentisert for å hente data fra ereg
    EREG_ERROR("EREG_ERROR"), // henting av organisasjonsdata feiler
    EREG_NOT_FOUND("EREG_NOT_FOUND"), // finner ikke organisasjon
    EREG_FORBIDDEN("EREG_FORBIDDEN"), // mangler tilgang til å hente data fra ereg

    // DOKARKIV
    DOKARKIV_UNAUTHORIZED("DOKARKIV_UNAUTHORIZED"), // ikke autentisert for å opprette journalpost
    DOKARKIV_ERROR("DOKARKIV_ERROR"), // oppretting av journalpost feiler
    DOKARKIV_FORBIDDEN("DOKARKIV_FORBIDDEN"), // mangler tilgang til å opprette journalpost

    // NORG2
    NORG2_UNAUTHORIZED("NORG2_UNAUTHORIZED"), // ikke autentisert for å hente data fra norg2
    NORG2_ERROR("NORG2_ERROR"), // henting av enheter feiler
    NORG2_FORBIDDEN("NORG2_FORBIDDEN"), // mangler tilgang til å hente data fra norg2

    // OPPGAVE
    OPPGAVE_UNAUTHORIZED("OPPGAVE_UNAUTHORIZED"), // ikke autentisert for å opprette/endre/hente oppgave
    OPPGAVE_ERROR("OPPGAVE_ERROR"), // oppretting/endring/henting av oppgave feiler
    OPPGAVE_FORBIDDEN("OPPGAVE_FORBIDDEN"), // mangler tilgang til å opprette/endre/hente oppgave
    OPPGAVE_COMPLETED("OPPGAVE_COMPLETED"), // oppgave er allerede ferdigstilt
    OPPGAVE_MISSING_JOURNALPOST("OPPGAVE_MISSING_JOURNALPOST"), // oppgave mangler journalpost
    OPPGAVE_NOT_FOUND("OPPGAVE_NOT_FOUND"), // finner ikke oppgave

    // PDL
    PDL_ERROR("PDL_ERROR"), // henting av persondata feiler (feil personnummer?)
    PDL_MISSING_AKTORID("PDL_MISSING_AKTORID"), // finner ingen aktørId for ident

    // SAF
    SAF_UNAUTHORIZED("SAF_UNAUTHORIZED"), // ikke autentisert for å hente data fra saf
    SAF_ERROR("SAF_ERROR"), // henting av dokument feiler
    SAF_FORBIDDEN("SAF_FORBIDDEN"), // mangler tilgang til å hente data fra saf
    SAF_NOT_FOUND("SAF_NOT_FOUND"), // finner ikke journalpost
    JOURNALPOST_MISSING_DOKUMENT("JOURNALPOST_MISSING_DOKUMENT"); // journalpost mangler dokument
}
