package no.nav.tilbakemeldingsmottak.consumer.saf.util;

import static java.lang.String.format;

import no.nav.tilbakemeldingsmottak.exceptions.saf.InvalidMappingToEnumFunctionalException;

public final class MappingUtil {

	private MappingUtil() {
	}

	public static <E extends Enum<E>> E stringToEnum(Class<E> enumClass, String enumName) {
		try {
			return enumName == null ? null : Enum.valueOf(enumClass, enumName);
		} catch (IllegalArgumentException e) {
			throw new InvalidMappingToEnumFunctionalException(format("%s er ikke en gyldig kodeverdi for %s", enumName, enumClass));
		}
	}
}
