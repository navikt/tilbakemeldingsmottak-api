package no.nav.tilbakemeldingsmottak.serviceklage;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class Question {
    @NonNull String id;
    @NonNull String text;
    @NonNull QuestionType type;
    String emit;
    String next;
    List<Answer> answers;
    Banner banner;
    boolean optional;
}
