package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


class PDFTextLocator : PDFTextStripper() {
    @Throws(IOException::class)
    override fun writeString(string: String, textPositions: List<TextPosition>) {
        if (string.contains(key_string!!)) {
            val text = textPositions[0]
            if (x == -1f) {
                x = text.xDirAdj
                y = text.yDirAdj
            }
        }
    }

    companion object {
        private var key_string: String? = null
        private var x: Float = -1f
        private var y: Float = -1f


        @Throws(IOException::class)
        fun getCoordiantes(bytes: ByteArray, phrase: String?, page: Int): Map<String, Float> {
            try {
                ByteArrayInputStream(bytes).use { stream ->
                    PDDocument.load(stream).use { document ->
                        key_string = phrase
                        val stripper: PDFTextStripper = PDFTextLocator()
                        stripper.sortByPosition = true
                        stripper.startPage = page
                        stripper.endPage = page
                        stripper.writeText(document, OutputStreamWriter(ByteArrayOutputStream()))

                        y = document.getPage(page - 1).mediaBox.height - y
                        if (x == -1f || y == -1f) {
                            throw RuntimeException("Fant ikke nøkkelordet $phrase på side $page (nullindeksert)")
                        }
                        return mapOf("x" to x, "y" to y)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException("Klarer ikke å åpne PDF for å kunne skjekke antall sider")
            }

        }
    }
}