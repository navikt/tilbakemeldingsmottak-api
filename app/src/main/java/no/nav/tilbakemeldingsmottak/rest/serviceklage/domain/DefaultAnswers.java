package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DefaultAnswers {

    String message;
    Map<String, String> answers;
}
