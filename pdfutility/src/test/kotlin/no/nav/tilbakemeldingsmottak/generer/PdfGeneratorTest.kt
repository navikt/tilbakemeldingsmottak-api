package no.nav.tilbakemeldingsmottak.generer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

internal class PdfGeneratorTest {


    @Test
    fun verifiserLagKvitteringPdf() {
        val klageRequest = lagMeldingsMap()
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        kotlin.test.assertEquals(1, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        //writeBytesToFile(klagePdf, "src/test/resources/delme4.pdf")
    }

    @Test
    fun verifiserLagPdf() {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund med tre ben og to haler."
        map.put(key,tekst)

        val klageRequest = map
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        kotlin.test.assertEquals(1, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        //writeBytesToFile(klagePdf, "src/test/resources/delme5.pdf")
    }

    private fun lagMeldingsMap(): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Ñunes har en hund med tre ben og to haler."
        for (i in 1..5 ) {
            var t = ""
            for (j in 0..i) t =  t + " " + tekst
            map.put(key+i, t)
        }
        map.put("En kjempelang ledetekst som strekker seg over flere linjer6", tekst)
        map.put("En kjempelang ledetekst som strekker seg over flere linjer7", tekst+ " " + tekst + " " + tekst)
        for (i in 8..10 ) {
            map.put(key+i, tekst+i)
        }
        map.put(key+11, null)
        map.put(key+12, tekst+12)
        map.put(key+13, "En kjempelang tekst Enkjempelangledetekstsomstrekkersegoverflerelinjer13Enkjempelangledetekstsomstrekkersegoverflerelinjer13" )
        map.put(key+14, "En kjempelang tekst som strekker seg over flere linjer14\n\tPunkt 1\n\tPunkt2\nEn kjempelang tekst")
        return map
    }

    @Test
    fun `Skal ikke kræsje med emojis`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund🐶med tre ben og to haler."
        map[key] = tekst

        // Så
        assertDoesNotThrow { PdfGenerator().genererPdf("Kvittering", null, map) }
    }

    @Test
    fun `Skal ikke kræsje på kontroll-karakterer`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Heisann \n\r\t\u0001\u0000sveisann"
        map[key] = tekst

        // Så
        assertDoesNotThrow { PdfGenerator().genererPdf("Kvittering", null, map) }
    }

    @Test
    fun `Skal ikke kræsje med StringIndexOutOfBoundsException`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund med tre ben og to haler. \n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n" +
                "\t\n\n"
        map[key] = tekst

        // Så
        assertDoesNotThrow { PdfGenerator().genererPdf("Kvittering", null, map) }
    }

    fun writeBytesToFile(data: ByteArray, filePath: String) {
        File(filePath).writeBytes(data)
    }

}

