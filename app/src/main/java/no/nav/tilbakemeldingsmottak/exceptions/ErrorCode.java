package no.nav.tilbakemeldingsmottak.exceptions;

// En samling av alle error codes som frontend klienter kan bruke for å vise brukervennlige feilmeldinger
public enum ErrorCode {
    GENERAL_ERROR("GENERAL_ERROR"),

    // EREG
    EREG_UNAUTHORIZED("EREG_UNAUTHORIZED"), // mangler tilgang til å hente data fra ereg
    EREG_ERROR("EREG_ERROR"), // validering av organisasjonsnummer feiler

    // DOKARKIV
    DOKARKIV_UNAUTHORIZED("DOKARKIV_UNAUTHORIZED"), // mangler tilgang til å opprette journalpost
    DOKARKIV_ERROR("DOKARKIV_ERROR"), // oppretting av journalpost feiler


    // NORG2
    NORG2_UNAUTHORIZED("NORG2_UNAUTHORIZED"), // mangler tilgang til å hente data fra norg2
    NORG2_ERROR("NORG2_ERROR"), // henting av enheter feiler

    // OPPGAVE
    OPPGAVE_UNAUTHORIZED("OPPGAVE_UNAUTHORIZED"), // mangler tilgang til å opprette/endre/hente oppgave
    OPPGAVE_ERROR("OPPGAVE_ERROR"), // oppretting/endring/henting av oppgave feiler

    // PDL
    PDL_ERROR("PDL_ERROR"), // henting av persondata feiler
    PDL_INGEN_AKTORID("PDL_INGEN_AKTORID"); // finner ingen aktørId for ident

    public final String value;

    ErrorCode(String value) {
        this.value = value;
    }
}
