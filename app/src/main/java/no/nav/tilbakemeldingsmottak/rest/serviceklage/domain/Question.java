package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class Question {
    @NonNull
    QuestionIdEnum id;
    QuestionType type;
    String emit;
    String next;
    List<Answer> answers;

    @JsonProperty("text")
    public String getText() {
        return this.id.text;
    }
}
