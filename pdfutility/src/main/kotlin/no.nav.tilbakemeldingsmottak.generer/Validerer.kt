package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.preflight.ValidationResult
import org.apache.pdfbox.preflight.exception.SyntaxValidationException
import org.apache.pdfbox.preflight.parser.PreflightParser
import org.apache.pdfbox.pdfwriter.compress.CompressParameters
import org.apache.pdfbox.Loader
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*


class Validerer() {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun isPDFa(bytes: ByteArray): Boolean {
        var result: ValidationResult? = null
        var document: PDDocument? = null
        var file: File? = null
        val fileName = "tmp_${UUID.randomUUID()}"

        try {
            file = File.createTempFile(fileName, ".pdf")
            document = Loader.loadPDF(bytes)
            document.save(file, CompressParameters.NO_COMPRESSION)
            result = PreflightParser.validate(file)
            logger.debug("PDF/A resultat: ${result.isValid}")
            if (result != null && !result.isValid) {
                logger.warn("Valideringsfeil: ${result.errorsList.map { it.details }.joinToString(";")}")
            }

            return result?.isValid == true
        } catch (ex: SyntaxValidationException) {
            logger.warn("Klarte ikke å lese fil for å sjekke om gyldig PDF/a, ${ex.message}", ex)
            if (result != null) {
                val sb = StringBuilder()
                for (error in result.errorsList) {
                    sb.append(error.errorCode + " : " + error.details + "\n")
                }
                logger.error("Feil liste:\n$sb")
            }
        } catch (ex: Error) {
            logger.warn("Klarte ikke å lese fil for å sjekke om gyldig PDF/a, ${ex.message}", ex)
        } finally {
            file?.deleteOnExit()
            file?.delete()
            document?.close()
        }

        return false
    }

}