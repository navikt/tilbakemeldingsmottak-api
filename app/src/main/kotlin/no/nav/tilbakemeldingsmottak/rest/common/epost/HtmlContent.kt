package no.nav.tilbakemeldingsmottak.rest.common.epost

class HtmlContent {
    var contentString = ""
        private set

    fun addParagraph(fieldname: String, content: String) {
        val paragraph = createParagraph(fieldname, content)
        contentString += paragraph
    }

    private fun createParagraph(fieldname: String, content: String): String {
        return String.format("<p><b>%s:</b> %s</p>", fieldname, content)
    }
}
