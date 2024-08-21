package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.Loader
import org.slf4j.LoggerFactory
import java.io.IOException


class AntallSider {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun finnAntallSider(bytes: ByteArray?): Int {
        if (bytes == null) return 0
        if (bytes.isEmpty()) return 0
        try {
            Loader.loadPDF(bytes).use { document ->
                return document.numberOfPages ?: 0
            }
        } catch (e: IOException) {
            logger.error("Klarer ikke å åpne PDF for å kunne skjekke antall sider")
            throw RuntimeException("Klarer ikke å åpne PDF for å kunne skjekke antall sider")
        }

    }

}
