package no.nav.tilbakemeldingsmottak.consumer.saf.util;

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;

import static java.lang.String.format;

public final class MappingUtil {

    private MappingUtil() {
    }

    public static <E extends Enum<E>> E stringToEnum(Class<E> enumClass, String enumName) {
        try {
            return enumName == null ? null : Enum.valueOf(enumClass, enumName);
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException(format("%s er ikke en gyldig kodeverdi for %s", enumName, enumClass), e);
        }
    }
}
