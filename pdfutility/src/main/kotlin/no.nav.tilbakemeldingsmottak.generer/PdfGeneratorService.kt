package no.nav.tilbakemeldingsmottak.generer

import com.github.jknack.handlebars.Handlebars
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import no.nav.tilbakemeldingsmottak.generer.modeller.ServiceklagePdfModell
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class PdfGeneratorService {
    private var handlebars: Handlebars = Handlebars()
    private val logger = LoggerFactory.getLogger(javaClass)

    fun genererServiceklagePdf(modell: ServiceklagePdfModell): ByteArray {
        val oppdatertModell = modell.fjernSpesielleKarakterer()

        try {
            // Generer html string
            val template = handlebars.compile("pdf/serviceklage").apply(oppdatertModell)
            return produserPdf(template)
        } catch (e: IOException) {
            logger.error("Feiler i serviceklage generering: {}", e.message)
            throw e
        }

    }

    // Generer PDF fra html string
    fun produserPdf(html: String): ByteArray {
        try {
            ByteArrayOutputStream().use { os ->
                val builder = PdfRendererBuilder()

                builder.useFastMode()
                builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_1_B)
                builder.useColorProfile(
                    PdfGeneratorService::class.java.getResource("/pdf/fonts/icc/sRGB.icc")?.readBytes()
                )
                builder.useFont(
                    PdfGeneratorService::class.java.getResource("/pdf/fonts/arial/arial.ttf")?.path?.let {
                        File(it)
                    }, "arial"
                )
                builder.useFont(
                    PdfGeneratorService::class.java.getResource("/pdf/fonts/arial/arialbd.ttf")?.path?.let {
                        File(it)
                    }, "arial"
                )
                builder.withHtmlContent(
                    html,
                    PdfGeneratorService::class.java.getResource("/pdf/")?.toExternalForm() ?: ""
                )

                builder.toStream(os)
                builder.run()
                return os.toByteArray()
            }
        } catch (e: Exception) {
            logger.error("Feiler i pdf-fil-generering", e)
            throw e
        }
    }


}