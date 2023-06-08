package no.nav.tilbakemeldingsmottak.consumer.saf.util;

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;

import static java.lang.String.format;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static void assertNotNullOrEmpty(String field, String value) {
        if (value == null || value.isEmpty()) {
            throw new ClientErrorException(format("Feltet %s kan ikke være null eller tomt. Fikk %s=%s", field, field, value));
        }
    }

    public static void assertJournalpostFieldNotNull(Class inputClass, Object value) {
        if (value == null) {
            throw new ClientErrorException(format("For journalposter kan feltet %s ikke være null eller tomt. Fikk %s=null", inputClass.getCanonicalName(), inputClass.getCanonicalName()));
        }
    }

    public static void assertJournalpostFieldNotNullOrEmpty(String field, String value) {
        if (value == null || value.isEmpty()) {
            throw new ClientErrorException(format("For journalposter kan feltet %s ikke være null eller tomt. Fikk %s=%s", field, field, value));
        }
    }

    public static void assertDokumentFieldNotNullOrEmpty(String field, String value) {
        if (value == null || value.isEmpty()) {
            throw new ClientErrorException(format("For dokumenter kan feltet %s ikke være null eller tomt. Fikk %s=%s", field, field, value));
        }
    }
}
