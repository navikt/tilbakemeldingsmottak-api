package no.nav.tilbakemeldingsmottak.serviceklage;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HentSkjemaResponse {
    String version;
    DefaultAnswers defaultAnswers;
    List<Question> questions;

}