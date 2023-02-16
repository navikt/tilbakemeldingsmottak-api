package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.preflight.PreflightDocument
import org.apache.pdfbox.preflight.ValidationResult
import org.apache.pdfbox.preflight.exception.SyntaxValidationException
import org.apache.pdfbox.preflight.parser.PreflightParser
import org.apache.pdfbox.preflight.utils.ByteArrayDataSource
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream


class Validerer() {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun isPDFa(bytes: ByteArray): Boolean {
        ByteArrayInputStream(bytes).use { byteArrayInputStream ->
            var result: ValidationResult? = null
            var document: PreflightDocument? = null
            try {
                val stream = ByteArrayDataSource(ByteArrayInputStream(bytes))
                val parser = PreflightParser(stream)
                parser.parse()
                document = parser.preflightDocument
                document.validate()
                result = document.result
                return result.isValid
            } catch (ex: SyntaxValidationException) {
                logger.error("Klarte ikke å lese fil for å sjekke om gyldig PDF/a, ${ex.message}")
                if (result != null) {
                    val sb = StringBuilder()
                    for (error in result.errorsList) {
                        sb.append(error.errorCode + " : " + error.details + "\n")
                    }
                    logger.error("Feil liste:\n" + sb.toString())
                }
            } catch (ex: Error) {
                logger.error("Klarte ikke å lese fil for å sjekke om gyldig PDF/a, ${ex.message}")
            } finally {
                if (document != null) {
                    document.close()
                }
            }
        }
        return false
    }
}