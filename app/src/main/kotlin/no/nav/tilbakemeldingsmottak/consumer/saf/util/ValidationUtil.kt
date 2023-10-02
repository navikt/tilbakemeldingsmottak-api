package no.nav.tilbakemeldingsmottak.consumer.saf.util

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException

object ValidationUtil {
    fun assertNotNullOrEmpty(field: String?, value: String?) {
        if (value.isNullOrEmpty()) {
            throw ClientErrorException(
                String.format(
                    "Feltet %s kan ikke være null eller tomt. Fikk %s=%s",
                    field,
                    field,
                    value
                )
            )
        }
    }

    fun assertJournalpostFieldNotNull(inputClass: Class<*>, value: Any?) {
        if (value == null) {
            throw ClientErrorException(
                String.format(
                    "For journalposter kan feltet %s ikke være null eller tomt. Fikk %s=null",
                    inputClass.getCanonicalName(),
                    inputClass.getCanonicalName()
                )
            )
        }
    }

    fun assertJournalpostFieldNotNullOrEmpty(field: String?, value: String?) {
        if (value.isNullOrEmpty()) {
            throw ClientErrorException(
                String.format(
                    "For journalposter kan feltet %s ikke være null eller tomt. Fikk %s=%s",
                    field,
                    field,
                    value
                )
            )
        }
    }

    fun assertDokumentFieldNotNullOrEmpty(field: String?, value: String?) {
        if (value.isNullOrEmpty()) {
            throw ClientErrorException(
                String.format(
                    "For dokumenter kan feltet %s ikke være null eller tomt. Fikk %s=%s",
                    field,
                    field,
                    value
                )
            )
        }
    }
}
