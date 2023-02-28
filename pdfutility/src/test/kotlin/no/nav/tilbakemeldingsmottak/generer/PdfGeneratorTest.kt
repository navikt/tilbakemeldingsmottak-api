package no.nav.tilbakemeldingsmottak.generer

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class PdfGeneratorTest {


    @Test
    fun verifiserLagKvitteringPdf() {
        val klageRequest = lagMeldingsMap()
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)
        writeBytesToFile(klagePdf, "src/test/resources/delme4.pdf")

        kotlin.test.assertEquals(2, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

    }

    @Test
    fun verifiserLagPdf() {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val tekst = "Jan har en hund med tre ben og to haler."
        map.put(key, tekst)

        val klageRequest = map
        val klagePdf = PdfGenerator().genererPdf("Kvittering", null, klageRequest)

        kotlin.test.assertEquals(2, AntallSider().finnAntallSider(klagePdf))
        val erPdfa = Validerer().isPDFa(klagePdf)
        assertTrue(erPdfa)

        //writeBytesToFile(klagePdf, "src/test/resources/delme5.pdf")
    }

    private fun lagMeldingsMap(): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        val key = "Key"
        val langTekst =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed congue purus eget ex pellentesque, at tempus sapien sollicitudin. Aenean dignissim, urna quis maximus fermentum, libero eros pretium enim, non ultrices ipsum augue eu dolor. Fusce congue mi magna, eu scelerisque ipsum commodo at. Sed vitae dignissim nunc. Ut gravida tellus eu ante elementum interdum. Curabitur in lorem elit. Quisque sit amet eros ut sem ornare elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce faucibus, mi eget luctus tempus, lorem lorem condimentum lectus, vel aliquam arcu urna vitae lorem. Morbi ut orci eget nisi fermentum rutrum a nec eros. Vestibulum ultricies magna tellus. Sed consequat justo erat, nec mollis ipsum lobortis a. Ut luctus feugiat est, nec elementum lacus suscipit a. Cras scelerisque arcu sed gravida pulvinar. Nam fringilla accumsan lacus sed iaculis.\n" +
                    "\n" +
                    "Fusce facilisis ligula et vestibulum placerat. Sed sit amet massa felis. Quisque convallis hendrerit porta. Etiam gravida condimentum mauris at vulputate. Suspendisse luctus, justo quis auctor viverra, eros est blandit elit, sit amet sagittis turpis urna vitae nunc. Etiam vel interdum dui. Nullam finibus turpis ipsum, quis facilisis magna sodales quis. Praesent tristique turpis in tellus mattis rutrum. Etiam scelerisque gravida pellentesque. Duis eget lacus id augue aliquet maximus eget at neque. Duis sagittis est mi, quis venenatis augue sagittis vel. Duis velit dui, elementum et nibh iaculis, blandit gravida nisl.\n" +
                    "\n" +
                    "Curabitur sed enim at magna suscipit gravida. Vestibulum varius mauris vitae tellus finibus, eu rhoncus ex volutpat. In eleifend condimentum ante. Donec mauris urna, ornare ac rutrum ut, egestas dictum est. Nam euismod leo sed ipsum rhoncus ornare. Aliquam vel egestas nisi. Nulla eget condimentum sapien. Pellentesque in est est. Donec pharetra turpis augue, ut auctor lectus lacinia sit amet. Ut tincidunt, tellus nec vulputate efficitur, leo nulla eleifend lectus, vel accumsan augue orci in nulla.\n" +
                    "\n" +
                    "Nulla lorem sapien, aliquam a augue quis, sollicitudin eleifend eros. Sed feugiat nisl mattis iaculis consequat. Sed rutrum sem mauris, quis fringilla nisl ullamcorper quis. Donec sagittis vulputate sem, ut gravida erat feugiat at. Aenean velit turpis, varius id dolor eu, tincidunt euismod nisl. Nulla orci nulla, vestibulum et elementum sit amet, semper sit amet lectus. Praesent elementum quam non velit pharetra, id tristique erat hendrerit. Proin lacinia dui urna, vitae tincidunt magna auctor a. Curabitur dapibus dui pellentesque ex tincidunt congue. Nulla sagittis, arcu ac convallis dictum, massa nunc aliquam orci, consequat elementum turpis eros ac nisl. Integer feugiat consectetur dolor, interdum facilisis nisi vestibulum vitae."
        val tekst = "Ñunes har en hund med tre ben og to haler."
        for (i in 1..5) {
            var t = ""
            for (j in 0..i) t = t + " " + tekst
            map.put(key + i, t)
        }
        val lengreTekst =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque eget rhoncus dui. Mauris vel velit eros. Aenean commodo ullamcorper volutpat. Nullam sollicitudin accumsan odio. Aenean vel justo quam. Fusce sagittis imperdiet tellus sit amet malesuada. Cras varius consequat ligula vitae faucibus. Praesent consequat varius neque, vel vehicula dui fermentum vel. Proin et elementum lectus.\n" +
                    "\n" +
                    "Integer ullamcorper, libero id bibendum consequat, ligula leo rhoncus nisi, eget porta nisl sapien et sem. Ut ultrices rutrum varius. Nunc eu metus tempus ipsum bibendum cursus sit amet sed eros. Etiam molestie risus risus, sit amet laoreet ligula blandit in. In dapibus quam in placerat posuere. Fusce pulvinar nisi id lacus bibendum, non commodo erat vulputate. Etiam quam ante, congue a mauris vel, varius tincidunt dui. Aliquam sodales sapien eget leo tristique, ac posuere tellus porttitor.\n" +
                    "\n" +
                    "Suspendisse congue ac lacus sit amet dictum. Donec pharetra mi non feugiat pellentesque. Suspendisse potenti. Sed ut sem non enim scelerisque tempus in nec dui. Sed ultricies faucibus purus quis molestie. Fusce convallis velit ac congue interdum. Nullam porta finibus sodales. Aliquam a mi eget quam euismod consequat.\n" +
                    "\n" +
                    "Vivamus faucibus, nisi vitae aliquam consectetur, quam eros viverra elit, eu efficitur ante leo in orci. Pellentesque in volutpat est, vel semper lorem. Sed quis tellus id dui viverra placerat non porttitor purus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Donec a dolor vel tortor elementum pharetra sed in massa. Integer sed elementum mauris. Mauris placerat nec ligula ut ullamcorper.\n" +
                    "\n" +
                    "Sed pretium elementum eros a consequat. Aliquam mollis neque et accumsan sollicitudin. Fusce ullamcorper enim urna. Etiam massa risus, sollicitudin in ullamcorper at, congue vitae sem. Mauris consequat scelerisque libero maximus eleifend. Donec mollis consectetur lectus, sit amet convallis erat sagittis sit amet. Duis eleifend quam id accumsan placerat. Mauris vitae tellus vitae nibh accumsan porta. Sed tempor dapibus mauris, non molestie ante scelerisque ac. Integer tincidunt justo vitae turpis malesuada tempus. Vestibulum pretium orci id dignissim posuere. Nullam id viverra"

        map.put("En", lengreTekst)
        map.put("E", tekst)
        map.put("En kjempelang ledetekst som strekker seg over flere linjer7", tekst + " " + tekst + " " + tekst)
        for (i in 8..10) {
            map.put(key + i, tekst + i)
        }
        map.put(key + 11, null)
        map.put(key + 12, tekst + 12)
        map.put("Lengre tekst", lengreTekst)

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

    fun writeBytesToFile(data: ByteArray, filePath: String) {
        File(filePath).writeBytes(data)
    }

}

