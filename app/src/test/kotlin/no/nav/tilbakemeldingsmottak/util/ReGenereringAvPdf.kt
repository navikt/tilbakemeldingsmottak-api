package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.Innmelder
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.*
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal class ReGenereringAvPdf {
    private fun createOpprettServiceklageRequestPrivatperson(klage: List<String?>): OpprettServiceklageRequest {
        return OpprettServiceklageRequest(
            paaVegneAv = PaaVegneAv.PRIVATPERSON,
            innmelder = Innmelder(navn = klage[32], telefonnummer = klage[33], personnummer = klage[3]),
            klagetyper = konverterTilKlageType(
                Arrays.stream(
                    klage[4]!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray<String>()).toList()),
            gjelderSosialhjelp = findSosialhjelpTypeText(klage[5]),
            klagetekst = klage[6],
            oenskerAaKontaktes = klage[25] != null,
            enhetsnummerPaaklaget = klage[13],
            klagetypeUtdypning = klage[30]
        )
    }

    private fun mapTilLocalDateTime(fremmetText: String?): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss")
        return LocalDateTime.parse(fremmetText!!.substring(0, 19), formatter)
    }

    private fun konverterTilKlageType(klageTypeStrenger: List<String>): List<Klagetyper> {
        val typer: MutableList<Klagetyper?> = LinkedList()
        for (klageStreng in klageTypeStrenger) {
            typer.add(findByText(klageStreng.trim { it <= ' ' }))
        }
        return typer.filterNotNull()
    }

    private fun findByText(text: String): Klagetyper? {
        for (v in Klagetyper.entries) {
            if (v.value.equals(text, ignoreCase = true)) {
                return v
            }
        }
        return null
    }

    private fun findSosialhjelpTypeText(text: String?): GjelderSosialhjelp {
        for (t in GjelderSosialhjelp.entries) {
            if (t.value.equals(text, ignoreCase = true)) {
                return t
            }
        }
        return GjelderSosialhjelp.VET_IKKE
    }

    private fun createOpprettServiceklageRequestPaaVegneAvPerson(klage: List<String?>): OpprettServiceklageRequest {
        return OpprettServiceklageRequest(paaVegneAv = PaaVegneAv.ANNEN_PERSON,
            innmelder = Innmelder(
                navn = klage[32],
                telefonnummer = klage[33],
                harFullmakt = true,
                personnummer = klage[3],
                rolle = "Fullmektig"
            ),
            paaVegneAvPerson = PaaVegneAvPerson(navn = klage[34], personnummer = klage[35]),
            klagetyper = konverterTilKlageType(
                Arrays.stream(
                    klage[4]!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray<String>()).toList()
            ),
            klagetekst = klage[6],
            oenskerAaKontaktes = klage[25] != null)
    }

    @Test
    fun generatePdfs() {
        try {
            val serviceklager = ReadXslxFile().readExcelFile("serviceklage-eks.xlsx")
            val pdfService = PdfService()
            var header = true
            for (klage in serviceklager.values) {
                if (header) {
                    header = false
                    continue
                }
                if ("Bruker selv som privatperson".equals(klage[10], ignoreCase = true)) {
                    val request = createOpprettServiceklageRequestPrivatperson(klage)
                    val klagePdf = pdfService.opprettServiceklagePdf(
                        request, "1" == klage[31], mapTilLocalDateTime(
                            klage[2]
                        )
                    )
                    //writeBytesToFile(klagePdf, "src/test/resources/serviceklage-jp-"+ klage.get(1) +".pdf");
                    assertTrue(klagePdf.isNotEmpty())
                } else if ("På vegne av en annen privatperson".equals(klage[10], ignoreCase = true)) {
                    val request = createOpprettServiceklageRequestPaaVegneAvPerson(klage)
                    val klagePdf = pdfService.opprettServiceklagePdf(
                        request, "1" == klage[31], mapTilLocalDateTime(
                            klage[2]
                        )
                    )
                    //writeBytesToFile(klagePdf, "src/test/resources/serviceklage-jp-"+ klage.get(1) +".pdf");
                    assertTrue(klagePdf.isNotEmpty())
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Lesing fra excel fil feilet" + e.message)
        }
    }

    private fun writeBytesToFile(data: ByteArray, filePath: String) {
        FileOutputStream(filePath).use { fos -> fos.write(data) }
    }
}
