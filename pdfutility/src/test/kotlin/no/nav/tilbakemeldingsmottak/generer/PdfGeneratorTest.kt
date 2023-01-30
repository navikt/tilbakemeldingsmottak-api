package no.nav.tilbakemeldingsmottak.generer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class PdfGeneratorTest {


    @Test
    fun verifiserLagKvitteringPdf() {
        val klageRequest = lagMeldingsMap()
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        kotlin.test.assertEquals(1, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        writeBytesToFile(klagePdf, "src/test/resources/delme4.pdf")
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
        map.put(key+13, "En kjempelang ledetekst Enkjempelangledetekstsomstrekkersegoverflerelinjer13Enkjempelangledetekstsomstrekkersegoverflerelinjer13" )
        return map
    }

    fun writeBytesToFile(data: ByteArray, filePath: String) {
        File(filePath).writeBytes(data)
    }

}

