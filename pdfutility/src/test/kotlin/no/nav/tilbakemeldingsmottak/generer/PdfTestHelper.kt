package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


class PDFTextLocator : PDFTextStripper() {
    private var keyString: String? = null
    private var x: Float = -1f
    private var y: Float = -1f

    override fun writeString(string: String, textPositions: List<TextPosition>) {
        if (string.contains(keyString!!)) {
            val text = textPositions[0]
            if (x == -1f) {
                x = text.xDirAdj
                y = text.yDirAdj
            }
        }
    }

    fun getCoordiantes(bytes: ByteArray, phrase: String?, page: Int): Map<String, Float> {
        try {
            ByteArrayInputStream(bytes).use { stream ->
                PDDocument.load(stream).use { document ->
                    keyString = phrase
                    sortByPosition = true
                    startPage = page
                    endPage = page
                    writeText(document, OutputStreamWriter(ByteArrayOutputStream()))

                    y = document.getPage(page - 1).mediaBox.height - y
                    if (x == -1f || y == -1f) {
                        throw RuntimeException("Fant ikke nøkkelordet $phrase på side $page")
                    }
                    return mapOf("x" to x, "y" to y)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Klarer ikke å åpne PDF for å finne koordinater")
        }

    }


}