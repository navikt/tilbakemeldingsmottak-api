package no.nav.tilbakemeldingsmottak.rest.common.domain

// Message er for utviklere
// ErrorCode er for frontend som skal mappe koden til en brukervennlig tekst
data class ErrorResponse(
    var message: String? = null,
    var errorCode: String? = null
)
