package no.nav.tilbakemeldingsmottak.generer

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.pdmodel.common.PDMetadata
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.*
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.xmpbox.XMPMetadata
import org.apache.xmpbox.schema.PDFAIdentificationSchema
import org.apache.xmpbox.type.BadFieldValueException
import org.apache.xmpbox.xml.XmpSerializer
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

private const val FONT_EKSTRA_STOR = 18
private const val FONT_SUB_HEADER = 16
private const val FONT_STOR = 14
private const val FONT_LITEN_HEADER = 13
private const val FONT_VANLIG = 10
private const val FONT_INFORMASJON = 11
private const val LINJEAVSTAND = 1.4f
private const val LINJEAVSTAND_HEADER = 1f
private const val LINJEAVSTAND_STOR = 30f

const val INNRYKK = 50f

class PdfGenerator {

    val KANAL_SERVICEKLAGESKJEMA_ANSWER = "Serviceklageskjema på nav.no"
    val STANDARD_LINJEAVSTAND = 1f


    fun genererPdf(tittel: String, varsling: String?, tekstMap: Map<String, String?>): ByteArray {
        return try {
            PdfBuilder(tittel)
                .startSide()
                .leggTilNavLogo()
                .startTekst()
                .flyttTilTopp()
                .leggTilHeaderMidstilt(tittel, FONT_SUB_HEADER)
                .flyttNedMed(LINJEAVSTAND_STOR)
                .leggTilHeaderMidstilt(varsling, FONT_STOR)
                .flyttNedMed(if (varsling != null) LINJEAVSTAND_STOR else 0f)
                .legTilTekster(tekstMap)
                .avsluttTekst()
                .avsluttSide()
                .leggTilFargeProfil()
                .leggTilXMPMetablokk()
                .generer()
        } catch (e: IOException) {
            throw RuntimeException("Kunne ikke generere PDF", e)
        }
    }

}

class PdfBuilder(private val tittel: String) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val COLOR_RESOURCE = "/fonts/icc/sRGB.icc"

    private val pdDocument = PDDocument()

    fun start() = PdfBuilder(tittel)

    fun startSide() = PageBuilder(this)

    fun getPdDocument() = pdDocument

    fun leggTilFargeProfil(): PdfBuilder {
        val colorProfile: InputStream? = PdfBuilder::class.java.getResourceAsStream(COLOR_RESOURCE)
        if (colorProfile == null) {
            logger.warn("Fant ikke ressursfil for setting av fargeprofil i folder $COLOR_RESOURCE. Dette er nødvendig for generering av PDF/A-1b. Ignorerer feilen")
            return this
        }
        val intent = PDOutputIntent(pdDocument, colorProfile)
        intent.info = "sRGB IEC61966-2.1"
        intent.outputCondition = "sRGB IEC61966-2.1"
        intent.outputConditionIdentifier = "sRGB IEC61966-2.1"
        intent.registryName = "http://www.color.org"
        pdDocument.getDocumentCatalog().addOutputIntent(intent)
        return this
    }

    fun leggTilXMPMetablokk(): PdfBuilder {
        val xmp = XMPMetadata.createXMPMetadata()
        try {
            val dc = xmp.createAndAddDublinCoreSchema()
            dc.title = tittel
            val id: PDFAIdentificationSchema = xmp.createAndAddPFAIdentificationSchema()
            id.part = 1
            id.conformance = "B"
            val serializer = XmpSerializer()
            ByteArrayOutputStream().use {
                serializer.serialize(xmp, it, true)
                val metadata = PDMetadata(pdDocument)
                metadata.importXMPMetadata(it.toByteArray())
                pdDocument.getDocumentCatalog().setMetadata(metadata)
            }
        } catch (e: BadFieldValueException) {
            // won't happen here, as the provided value is valid
            throw IllegalArgumentException(e)
        }
        return this
    }

    @Throws(IOException::class)
    fun generer(): ByteArray {
        ByteArrayOutputStream().use { stream ->
            pdDocument.save(stream)
            pdDocument.close()
            return stream.toByteArray()
        }
    }
}

class PageBuilder(private val pdfBuilder: PdfBuilder) {
    private var page = PDPage(PDRectangle.A4)
    private var logo: PDImageXObject
    private var contentStream: PDPageContentStream
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        pdfBuilder.getPdDocument().addPage(page)
        try {
            logo = JPEGFactory.createFromStream(pdfBuilder.getPdDocument(), PageBuilder::class.java.getResourceAsStream("/icons/navlogo.jpg"))
            contentStream = PDPageContentStream(pdfBuilder.getPdDocument(), page, AppendMode.APPEND, true)
        } catch (e: IOException) {
            throw Exception("navlogo.jpg er fjernet fra prosjektet, eller feilet under åpning av contentStream.", e)
        }
    }

    fun getFont(path: String): PDFont {
        try	{
            val inputStream = ClassPathResource(path).getInputStream()
            return PDType0Font.load(getPdDocument(), inputStream)
        } catch (ex: IOException) {
            logger.warn("Fant ikke ressursfil $path", ex.message)
            throw RuntimeException("Fant ikke ressursfil $path. Feil ved generering av PDF")
        }
    }

    fun getPdDocument() = pdfBuilder.getPdDocument()

    fun getContentStream() = contentStream

    fun getPage() = page

    @Throws(IOException::class)
    fun leggTilNavLogo(): PageBuilder {
        contentStream.drawImage(logo, (page.mediaBox.width - logo.width) / 2, 750f)
        return this
    }

    @Throws(IOException::class)
    fun startTekst() = TextBuilder(this)

    @Throws(IOException::class)
    fun avsluttSide(): PdfBuilder {
        contentStream.close()
        return pdfBuilder
    }
}

class TextBuilder(private val pageBuilder: PageBuilder) {
    private val tekstBredde: Float
    private val pageWidth: Float
    private val cellWidth: Float
    private val ledetekstBredde: Float
    private val contentStream = pageBuilder.getContentStream()

    private val logger = LoggerFactory.getLogger(javaClass)
    private val regex = Regex("\t")
    private var arialFont: PDFont? = null
    private var arialBoldFont: PDFont? = null
    private val ARIAL_FONT_PATH = "fonts/arial/arial.ttf"
    private val ARIALBOLD_FONT_PATH = "fonts/arial/arialbd.ttf"

    init {
        contentStream.beginText()
        pageWidth = pageBuilder.getPage().mediaBox.width
        tekstBredde = pageWidth - INNRYKK * 2
        ledetekstBredde = tekstBredde / 3
        cellWidth = pageWidth - ledetekstBredde - INNRYKK * 2
    }

    private fun hentArial(): PDFont {
        if (arialFont == null) {
            arialFont = pageBuilder.getFont(ARIAL_FONT_PATH)
            return arialFont as PDFont
        } else {
            return arialFont as PDFont
        }
    }
    private fun hentArialBold(): PDFont {
        if (arialBoldFont == null) {
            arialBoldFont = pageBuilder.getFont(ARIALBOLD_FONT_PATH)
            return arialBoldFont as PDFont
        } else {
            return arialBoldFont as PDFont
        }
    }

    @Throws(IOException::class)
    fun leggTilHeaderMidstilt(tekst: String?, storrelse: Int): TextBuilder {
        if (tekst != null) {
            val useFont = hentArialBold()
            contentStream.setFont(useFont, storrelse.toFloat())
            skrivLedetekstOgTekst(tekst, useFont, storrelse, LINJEAVSTAND_HEADER, true)
        }
        return this
    }

    @Throws(IOException::class)
    fun leggTilHeader(tekst: String?, storrelse: Int): TextBuilder {
        if (tekst != null) {
            val useFont = hentArialBold()
            contentStream.setFont(useFont, storrelse.toFloat())
            skrivLedetekstOgTekst(tekst, useFont, storrelse, LINJEAVSTAND_HEADER, false)
        }
        return this
    }

    @Throws(IOException::class)
    private fun skrivLedetekstOgTekst(
        ledetekst: String, ledetekstFont: PDFont,
        tekst: String, font: PDFont,
        fontSize: Int,
        linjeavstand: Float,
        midstilt: Boolean
    ) {
        val height = font.fontDescriptor.fontBoundingBox.height / 1000 * fontSize
        skrivTekst(ledetekst, ledetekstFont, fontSize, midstilt, linjeavstand, height, ledetekstBredde)

        contentStream.newLineAtOffset(ledetekstBredde, 0f)
        skrivTekst(tekst, font, fontSize, midstilt, linjeavstand, height)
        flyttNedMed(height * linjeavstand)
        contentStream.newLineAtOffset(-ledetekstBredde, 0f)
    }

    private fun skrivTekst (
        tekst: String,
        font: PDFont,
        fontSize: Int,
        midstilt: Boolean,
        linjeavstand: Float,
        height: Float,
        bredde: Float? = null
    ) {
        contentStream.setFont(font, fontSize.toFloat())
        val konvertertTekst = regex.replace(tekst," ")
        var startIndex = 0
        while (startIndex < konvertertTekst.length - 1) {
            if (startIndex > 0) flyttNedMed(height * linjeavstand)
            var linje = finnLinje(konvertertTekst.substring(startIndex), font, fontSize, bredde)
            if (linje.isEmpty()) {
                val antallkarakterer = 60
                linje = konvertertTekst.substring(startIndex, startIndex+antallkarakterer-1)
                contentStream.showText(konvertertTekst.substring(startIndex, startIndex+antallkarakterer))
            } else if (midstilt) {
                skrivLinjeMidtstilt(linje, font, fontSize)
            } else {
                contentStream.showText(linje)
            }
            startIndex = startIndex + linje.length + 1
        }

    }


    @Throws(IOException::class)
    fun leggTilLedetekstOgTekst(ledetekst: String, tekst: String?, storrelse: Int, linjeavstand: Float): TextBuilder {
        if (tekst != null) {
            skrivLedetekstOgTekst(ledetekst+": ", hentArialBold(), tekst, hentArial(), storrelse, linjeavstand, false)
        }
        return this
    }

    @Throws(IOException::class)
    fun legTilTekster(tekstMap: Map<String, String?>): TextBuilder {
        for (key in tekstMap.keys)  {
            leggTilLedetekstOgTekst(key, tekstMap.get(key), FONT_VANLIG, LINJEAVSTAND/1.4f)
        }
        return this
    }


    @Throws(IOException::class)
    fun leggTilTekstMidtstilt(tekst: String?, storrelse: Int, linjeavstand: Float): TextBuilder {
        if (tekst != null) {
            val useFont = hentArial()
            contentStream.setFont(useFont, storrelse.toFloat())
            skrivLedetekstOgTekst(tekst, useFont, storrelse, linjeavstand, true)
        }
        return this
    }

    @Throws(IOException::class)
    private fun skrivLedetekstOgTekst(
        tekst: String,
        font: PDFont,
        fontSize: Int,
        linjeavstand: Float,
        midstilt: Boolean,
        bredde: Float? = null,
        initiellStartIndex: Int? = 0
    ) {
        val konvertertTekst = regex.replace(tekst," ")
        var startIndex = initiellStartIndex ?: 0
        val height = font.fontDescriptor.fontBoundingBox.height / 1000 * fontSize
        while (startIndex < konvertertTekst.length - 1) {
            val linje = finnLinje(konvertertTekst.substring(startIndex), font, fontSize, bredde)
            if (linje.isEmpty()) {
                contentStream.showText(konvertertTekst.substring(startIndex, startIndex + 29))
                startIndex = startIndex + 30
            }
            if (midstilt) {
                skrivLinjeMidtstilt(linje, font, fontSize)
            } else {
                contentStream.showText(linje)
            }
            flyttNedMed(height * linjeavstand)
            startIndex = startIndex + linje.length
        }
    }


    private fun finnLinje(tekst: String, font: PDFont, fontSize: Int, bredde: Float?): String {
        val split = tekst.split(" ").toTypedArray()
        val sb = StringBuilder()
        for (ord in split) {
            val nesteOrd = if (sb.toString().isEmpty()) ord else " $ord"
            if (leggTilOrdFeiler(sb, nesteOrd, font, fontSize, bredde)) {
                return sb.toString()
            } else {
                sb.append(nesteOrd)
            }
        }
        return sb.toString()
    }

    private fun tekstBredde(tekst: String, font: PDFont, fontSize: Int): Float {
        return font.getStringWidth(tekst) / 1000.0f * fontSize
    }


    private fun leggTilOrdFeiler(sb: StringBuilder, nestOrd: String, font: PDFont, fontSize: Int, bredde: Float? = null): Boolean {
        try {
            val linje = sb.toString() + nestOrd
            val linjeBredde = font.getStringWidth(linje) / 1000.0f * fontSize
            val tmp = bredde ?: cellWidth
            return linjeBredde > tmp
        } catch (e: IOException) {
            return false
        } catch (e2: IllegalArgumentException) {
            logger.warn("Feil i forbindelse med av generering av PDF med $sb + $nestOrd")
            return false
        }
    }

    @Throws(IOException::class)
    private fun skrivLinjeMidtstilt(linje: String, font: PDFont, fontSize: Int) {
        val width = font.getStringWidth(linje) / 1000.0f * fontSize
        contentStream.newLineAtOffset((pageWidth - width - INNRYKK * 2) / 2, 0f)
        contentStream.showText(linje)
        contentStream.newLineAtOffset((pageWidth - width - INNRYKK * 2) / -2, 0f)
    }

    @Throws(IOException::class)
    fun flyttTilTopp(): TextBuilder {
        contentStream.newLineAtOffset(INNRYKK, 700f)
        return this
    }

    @Throws(IOException::class)
    fun flyttNedMed(piksler: Float): TextBuilder {
        contentStream.newLineAtOffset(0f, -1 * piksler)
        return this
    }

    @Throws(IOException::class)
    fun avsluttTekst(): PageBuilder {
        contentStream.endText()
        return pageBuilder
    }

    @Throws(IOException::class)
    fun leggTilLedetekstOgTekst(
        tekst: String?,
        flyttNedMed: Int
    ): TextBuilder {
        if (tekst != null) {
            leggTilHeaderMidstilt(tekst, FONT_SUB_HEADER)
                .flyttNedMed(flyttNedMed.toFloat())
        }
        return this
    }


}
