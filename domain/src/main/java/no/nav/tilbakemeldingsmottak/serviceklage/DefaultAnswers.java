package no.nav.tilbakemeldingsmottak.serviceklage;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultAnswers {

    String message;
    Map<String, String> answers;
}
