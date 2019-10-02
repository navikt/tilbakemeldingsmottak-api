package no.nav.tilbakemeldingsmottak.rest.common.epost;

import lombok.Getter;

public class HtmlContent {

    @Getter
    private String contentString = "";

    public void addParagraph(String fieldname, String content) {
        String paragraph = createParagraph(fieldname, content);
        this.contentString += paragraph;
    }

    private String createParagraph(String fieldname, String content) {
        return String.format("<p><b>%s:</b> %s</p>", fieldname, content);
    }
}
