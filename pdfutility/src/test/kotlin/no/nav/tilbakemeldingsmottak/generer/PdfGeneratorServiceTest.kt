package no.nav.tilbakemeldingsmottak.generer

import no.nav.tilbakemeldingsmottak.generer.modeller.ServiceklagePdfModell
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals

class PdfGeneratorServiceTest {

    // Lengre tekst for å teste pdf'er med flere sider og linjeskift
    val loremIpsum1 =
        "Lorem ipsum dolor sit amet,\n \n consectetur adipiscing elit. Phasellus elit justo, venenatis in leo eu, porta tempor ex. In dictum mollis purus a porttitor. Etiam convallis, tortor a porta dictum, lacus elit faucibus metus, eget tempus eros metus quis tellus. Curabitur sollicitudin lacus ac tristique tristique. Sed elementum sit amet nulla quis pulvinar. Etiam pellentesque molestie dapibus. In nec mauris ex. Proin pharetra justo quis commodo pharetra. Sed vulputate malesuada enim, sit amet elementum erat tincidunt quis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer varius tincidunt pellentesque. Donec nec ante vitae augue sollicitudin interdum nec sit amet elit. Mauris fermentum elit sed justo porttitor iaculis. Duis rhoncus, arcu non luctus molestie, diam arcu varius purus, sit amet maximus ex velit quis ipsum. Vestibulum ut risus semper dui vestibulum congue at at nisl.\n" +
                "\n" +
                "Duis molestie ornare justo. Quisque gravida dui ac dolor porta porttitor. Vestibulum risus metus, blandit sed placerat pharetra, euismod eu odio. Phasellus mattis dui viverra urna egestas malesuada. Donec justo justo, sodales sed elit vel, elementum scelerisque erat. Curabitur ultricies nibh ut arcu mollis scelerisque. In eu mi ultrices, consectetur tellus pulvinar, sagittis sapien. In tempus nunc a dui condimentum accumsan. Nam laoreet augue at sem mollis, quis euismod turpis molestie. Aliquam maximus pellentesque placerat. Quisque vehicula pharetra est, vitae ornare enim posuere non. Integer pellentesque arcu eu est auctor volutpat. Integer et enim nisi. Cras vehicula augue ac diam egestas, ac commodo lacus porta.\n" +
                "\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla non aliquet justo. Mauris pharetra suscipit felis, id varius nulla efficitur et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Curabitur vel lectus venenatis, fermentum ipsum at, eleifend nibh. In rhoncus accumsan accumsan. In pharetra mauris lorem, pulvinar maximus erat feugiat in. Cras dui nunc, facilisis quis elit at, tincidunt placerat turpis. Aliquam ultricies magna quis felis scelerisque, sed fermentum diam fringilla. Fusce dui elit, laoreet quis tellus at, ultrices auctor velit. Praesent et tortor enim. Quisque sapien mi, tempus quis rutrum ac, ultricies non sem. Aenean nec ante nisi. Duis posuere dolor nisi, at eleifend neque luctus sed. Quisque volutpat consectetur magna ut ullamcorper. Ut ultrices leo quis sem ullamcorper congue."


    // Tester litt forskjellige varianter av tekster og null verdier
    private fun lagMeldingsMap(): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Ñunes har en hund med tre ben og to haler."
        for (i in 1..5) {
            var t = ""
            for (j in 0..i) t = "$t $tekst"
            map[key + i] = t
        }
        map["En kjempelang ledetekst som strekker seg over flere linjer6"] = tekst
        map["En kjempelang ledetekst som strekker seg over flere linjer7"] = "$tekst $tekst $tekst"
        for (i in 8..10) {
            map[key + i] = tekst + i
        }
        map[key + 11] = null
        map[key + 12] = tekst + 12
        map[key + 13] =
            "En kjempelang tekst Enkjempelangledetekstsomstrekkersegoverflerelinjer13Enkjempelangledetekstsomstrekkersegoverflerelinjer13"
        map[key + 14] =
            "En kjempelang tekst som strekker seg over flere linjer14\n\tPunkt 1\n\tPunkt2\nEn kjempelang tekst"

        return map
    }

    @Test
    fun `Skal generere pdf med 1 side og som er pdfa 1b`() {
        // Gitt
        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = lagMeldingsMap())

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)
        // Write PDF file to disk for manual inspection
        //writeBytesToFile(pdf, "delme.pdf")


        // Så
        val erPdfa = Validerer().isPDFa(pdf)

        assertNotNull(pdf)
        assertEquals(1, AntallSider().finnAntallSider(pdf))
        assertTrue(erPdfa)

    }

    @Test
    fun `Skal lage to sider når teksten går over 2 sider`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum1

        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = map)

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)

        // Så
        assertNotNull(pdf)
        assertEquals(2, AntallSider().finnAntallSider(pdf))

//        writeBytesToFile(pdf, "test2.pdf")

    }

    @Test
    fun `Skal lage pdf med riktig tekst`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum1

        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = map)

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)
        val tittelPosisjon = PDFTextLocator().getCoordiantes(pdf, "Tittel", 1)
        val subtittelPosisjon = PDFTextLocator().getCoordiantes(pdf, "Subtittel", 1)
        val keyPosisjon = PDFTextLocator().getCoordiantes(pdf, "key1", 1)

        // Så
        assertNotNull(pdf)
        assertEquals(2, AntallSider().finnAntallSider(pdf))
        assertEquals(53.25f, keyPosisjon["x"], "Skal ha x-posisjon på 53.25 (margin)")
        assertEquals(690.8625f, tittelPosisjon["y"], "Skal ha y-posisjon på 690.8625 (toppen av siden)")
        assertEquals(643.7625f, subtittelPosisjon["y"], "Skal ha y-posisjon på 643.7625 (under tittel)")

//        writeBytesToFile(pdf, "test3.pdf")

    }

    @Test
    fun `Skal ha riktig sidetall for hver side`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        map["key1"] = loremIpsum1
        map["key2"] = loremIpsum1
        map["key3"] = loremIpsum1
        map["key4"] = loremIpsum1

        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = map)

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)

        // Så
        assertEquals(3, AntallSider().finnAntallSider(pdf))

        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "Side 1", 1) }
        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "Side 2", 2) }
        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "Side 3", 3) }

        assertThrows<RuntimeException> { PDFTextLocator().getCoordiantes(pdf, "Side 1", 3) }
        assertThrows<RuntimeException> { PDFTextLocator().getCoordiantes(pdf, "Side 2", 3) }
        assertThrows<RuntimeException> { PDFTextLocator().getCoordiantes(pdf, "Side 3", 1) }

//        writeBytesToFile(pdf, "test3.pdf")

    }

    @Test
    fun `Skal ikke kræsje med emojis og skrive string uten emoji`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund🐶 med tre ben og to haler."
        map[key] = tekst

        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = map)

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)

        // Så
        assertDoesNotThrow { PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell) }
        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "Jan har en hund med tre ben og to haler.", 1) }

//        writeBytesToFile(pdf, "test4.pdf")

    }

    @Test
    fun `Skal ikke kræsje på kontroll-karakterer`() {
        // Gitt
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Heisann \n\r\t\u0001\u0000sveisann"
        map[key] = tekst

        val serviceklagePdfModell =
            ServiceklagePdfModell(tittel = "Tittel", subtittel = "Subtittel", data = map)

        // Når
        val pdf = PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell)

        // Så
        assertDoesNotThrow { PdfGeneratorService().genererServiceklagePdf(serviceklagePdfModell) }
        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "Heisann", 1) }
        assertDoesNotThrow { PDFTextLocator().getCoordiantes(pdf, "sveisann", 1) }

//        writeBytesToFile(pdf, "test5.pdf")
    }

    // Skriv til fil for å se på resultatet
    fun writeBytesToFile(data: ByteArray, filePath: String) {
        File(filePath).writeBytes(data)
    }

}