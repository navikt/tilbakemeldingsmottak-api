package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageGjelderSosialhjelp.VET_IKKE
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.ANNEN_PERSON
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.PRIVATPERSON
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@kotlin.ExperimentalStdlibApi
internal class ReGenereringAvPdf {
    private fun createOpprettServiceklageRequestPrivatperson(klage: List<String?>): OpprettServiceklageRequest {
        return OpprettServiceklageRequest(
            paaVegneAv = PRIVATPERSON,
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

    private fun konverterTilKlageType(klageTypeStrenger: List<String>): List<OpprettServiceklageKlagetype> {
        val typer: MutableList<OpprettServiceklageKlagetype?> = LinkedList()
        for (klageStreng in klageTypeStrenger) {
            typer.add(findByText(klageStreng.trim { it <= ' ' }))
        }
        return typer.filterNotNull()
    }

    private fun findByText(text: String): OpprettServiceklageKlagetype? {
        for (v in OpprettServiceklageKlagetype.entries) {
            if (v.value.equals(text, ignoreCase = true)) {
                return v
            }
        }
        return null
    }

    private fun findSosialhjelpTypeText(text: String?): OpprettServiceklageGjelderSosialhjelp {
        for (t in OpprettServiceklageGjelderSosialhjelp.entries) {
            if (t.value.equals(text, ignoreCase = true)) {
                return t
            }
        }
        return VET_IKKE
    }

    private fun createOpprettServiceklageRequestPaaVegneAvPerson(klage: List<String?>): OpprettServiceklageRequest {
        return OpprettServiceklageRequest(paaVegneAv = ANNEN_PERSON,
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
