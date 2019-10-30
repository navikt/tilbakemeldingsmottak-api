package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HentSkjemaResponse {
    String version;
    DefaultAnswers defaultAnswers;
    List<Question> questions;

}