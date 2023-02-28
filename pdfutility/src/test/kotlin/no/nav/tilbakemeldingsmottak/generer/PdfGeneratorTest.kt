package no.nav.tilbakemeldingsmottak.generer

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

internal class PdfGeneratorTest {
    val loremIpsum1 =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus elit justo, venenatis in leo eu, porta tempor ex. In dictum mollis purus a porttitor. Etiam convallis, tortor a porta dictum, lacus elit faucibus metus, eget tempus eros metus quis tellus. Curabitur sollicitudin lacus ac tristique tristique. Sed elementum sit amet nulla quis pulvinar. Etiam pellentesque molestie dapibus. In nec mauris ex. Proin pharetra justo quis commodo pharetra. Sed vulputate malesuada enim, sit amet elementum erat tincidunt quis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer varius tincidunt pellentesque. Donec nec ante vitae augue sollicitudin interdum nec sit amet elit. Mauris fermentum elit sed justo porttitor iaculis. Duis rhoncus, arcu non luctus molestie, diam arcu varius purus, sit amet maximus ex velit quis ipsum. Vestibulum ut risus semper dui vestibulum congue at at nisl.\n" +
                "\n" +
                "Duis molestie ornare justo. Quisque gravida dui ac dolor porta porttitor. Vestibulum risus metus, blandit sed placerat pharetra, euismod eu odio. Phasellus mattis dui viverra urna egestas malesuada. Donec justo justo, sodales sed elit vel, elementum scelerisque erat. Curabitur ultricies nibh ut arcu mollis scelerisque. In eu mi ultrices, consectetur tellus pulvinar, sagittis sapien. In tempus nunc a dui condimentum accumsan. Nam laoreet augue at sem mollis, quis euismod turpis molestie. Aliquam maximus pellentesque placerat. Quisque vehicula pharetra est, vitae ornare enim posuere non. Integer pellentesque arcu eu est auctor volutpat. Integer et enim nisi. Cras vehicula augue ac diam egestas, ac commodo lacus porta.\n" +
                "\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla non aliquet justo. Mauris pharetra suscipit felis, id varius nulla efficitur et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Curabitur vel lectus venenatis, fermentum ipsum at, eleifend nibh. In rhoncus accumsan accumsan. In pharetra mauris lorem, pulvinar maximus erat feugiat in. Cras dui nunc, facilisis quis elit at, tincidunt placerat turpis. Aliquam ultricies magna quis felis scelerisque, sed fermentum diam fringilla. Fusce dui elit, laoreet quis tellus at, ultrices auctor velit. Praesent et tortor enim. Quisque sapien mi, tempus quis rutrum ac, ultricies non sem. Aenean nec ante nisi. Duis posuere dolor nisi, at eleifend neque luctus sed. Quisque volutpat consectetur magna ut ullamcorper. Ut ultrices leo quis sem ullamcorper congue."

    val loremIpsum2 =
        "Sed id fermentum dui. Aliquam diam augue, feugiat vestibulum purus ac, auctor consequat quam. Curabitur elit nisl, pulvinar vel justo id, pretium sodales tellus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas neque turpis, fermentum ullamcorper nulla vel, rutrum rhoncus est. Ut quis arcu sit amet libero viverra accumsan id sit amet nisl. Donec lacinia, turpis eget euismod auctor, mi mauris efficitur tortor, vitae condimentum lorem purus id quam. Morbi imperdiet commodo neque eu molestie. Nulla tristique accumsan leo.\n"
    val loremIpsum3 =
        "Proin pretium eleifend felis eget dictum. Integer hendrerit suscipit quam, vitae tempor ex hendrerit sit amet. Etiam sollicitudin est ac dui porta aliquam. Sed tincidunt, quam id finibus dignissim, justo nulla malesuada ex, in euismod ipsum nulla sed nibh. Duis dictum ligula sed suscipit laoreet. Suspendisse non neque dictum, tristique lacus sit amet, condimentum nulla. Suspendisse id maximus odio. Vivamus vulputate est et arcu fringilla, in eleifend felis dictum. Vivamus ac auctor ante. Aliquam facilisis venenatis aliquet. Nulla non egestas lacus, sed vehicula dolor.\n"

    @Test
    fun verifiserLagKvitteringPdf() {
        val klageRequest = lagMeldingsMap()
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        assertEquals(1, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        //writeBytesToFile(klagePdf, "src/test/resources/delme4.pdf")
    }

    @Test
    fun verifiserLagPdf() {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund med tre ben og to haler."
        map.put(key, tekst)

        val klageRequest = map
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        assertEquals(1, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        //writeBytesToFile(klagePdf, "src/test/resources/delme5.pdf")
    }

    private fun lagMeldingsMap(): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Ñunes har en hund med tre ben og to haler."
        for (i in 1..5) {
            var t = ""
            for (j in 0..i) t = t + " " + tekst
            map.put(key + i, t)
        }
        map.put("En kjempelang ledetekst som strekker seg over flere linjer6", tekst)
        map.put("En kjempelang ledetekst som strekker seg over flere linjer7", tekst + " " + tekst + " " + tekst)
        for (i in 8..10) {
            map.put(key + i, tekst + i)
        }
        map.put(key + 11, null)
        map.put(key + 12, tekst + 12)
        map.put(
            key + 13,
            "En kjempelang tekst Enkjempelangledetekstsomstrekkersegoverflerelinjer13Enkjempelangledetekstsomstrekkersegoverflerelinjer13"
        )
        map.put(
            key + 14,
            "En kjempelang tekst som strekker seg over flere linjer14\n\tPunkt 1\n\tPunkt2\nEn kjempelang tekst"
        )
        return map
    }

    @Test
    fun `Skal lage 2 sider når teksten går over 2 sider`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum1

        // Når
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, map)
        val textPosition = PDFTextLocator().getCoordiantes(klagePdf, "augue sollicitudin", 2)

        // Så
        assertEquals(2, AntallSider().finnAntallSider(klagePdf))
        assertEquals(215.09187f, textPosition["x"], "Skal ha x-posisjon med 215.09187 (til høyre for ledetekst)")
        assertEquals(791.8898f, textPosition["y"], "Skal ha y-posisjon med 791.8898 (toppen av siden)")
    }

    @Test
    fun `Skal lage 2 sider når neste key-value er på ny side`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum2
        map["key3"] = loremIpsum3

        // Når
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, map)
        val textPosition = PDFTextLocator().getCoordiantes(klagePdf, "key3", 2)

        // Så
        assertEquals(2, AntallSider().finnAntallSider(klagePdf))
        assertEquals(50f, textPosition["x"], "Skal ha x-posisjon med 50 (INNRYKK)")
        assertEquals(791.8898f, textPosition["y"], "Skal ha y-posisjon med 791.8898 (toppen av siden)")
    }

    @Test
    fun `Skal lage 3 sider når teksten går over 3 sider`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum1
        map["key3"] = loremIpsum1
        map["key4"] = loremIpsum1


        // Når
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, map)

        // Så
        assertEquals(3, AntallSider().finnAntallSider(klagePdf))
    }

    @Test
    fun `Skal lage 2 sider når man bruker varsling`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum2
        map["key3"] = loremIpsum3

        // Når
        val klagePdf = PdfGenerator().genererPdf("Kvittering", "Heisann", map)
        val textPosition = PDFTextLocator().getCoordiantes(klagePdf, "key3", 2)

        // Så
        assertEquals(2, AntallSider().finnAntallSider(klagePdf))
        assertEquals(50f, textPosition["x"], "Skal ha x-posisjon med 50 (INNRYKK)")
        assertEquals(
            751.9728f,
            textPosition["y"],
            "Skal ha y-posisjon med 751.9728 (litt lengre ned enn toppen av siden pga varsling)"
        )
    }


    fun writeBytesToFile(data: ByteArray, filePath: String) {
        File(filePath).writeBytes(data)
    }

}

