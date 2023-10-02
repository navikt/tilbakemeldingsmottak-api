package no.nav.tilbakemeldingsmottak.generer.modeller

import no.nav.tilbakemeldingsmottak.utils.PdfUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ServiceklagePdfModell(
    val tittel: String,
    val subtittel: String?,
    val data: Map<String, String?>,
    val dato: String? = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
) {
    fun fjernSpesielleKarakterer(): ServiceklagePdfModell {
        val tittel = PdfUtils.fjernSpesielleKarakterer(tittel)
        val subtittel = PdfUtils.fjernSpesielleKarakterer(subtittel)
        val data = data.mapValues {
            PdfUtils.fjernSpesielleKarakterer(it.value)
        }
        return ServiceklagePdfModell(
            tittel = tittel ?: "",
            subtittel = subtittel ?: "",
            data = data,
            dato = dato
        )
    }
}