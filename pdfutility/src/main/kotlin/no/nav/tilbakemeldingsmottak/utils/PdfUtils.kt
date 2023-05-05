package no.nav.tilbakemeldingsmottak.utils

import java.util.regex.Pattern

class PdfUtils {
    companion object {
        fun fjernSpesielleKarakterer(text: String?): String? {
            if (text == null) return null

            val regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n]"
            val pattern: Pattern = Pattern.compile(
                regex,
                Pattern.UNICODE_CHARACTER_CLASS
            )
            val matcher = pattern.matcher(text)
            return matcher.replaceAll("").trim()
        }
    }

}