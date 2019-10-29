package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.QuestionConstants.questionMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class Question {
    @NonNull
    String id;
    QuestionType type;
    String emit;
    String next;
    List<Answer> answers;

    @JsonProperty("text")
    public String getText() {
        return questionMap.get(id);
    }
}
