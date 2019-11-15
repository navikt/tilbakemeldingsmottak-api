package no.nav.tilbakemeldingsmottak.util;

import lombok.NoArgsConstructor;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Question;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public final class SkjemaUtils {

    public static Optional<Question> getQuestionById(List<Question> questions, String id) {
        return questions.stream()
                .map(question -> id.equals(question.getId())
                        ? Optional.of(question)
                        : Optional.ofNullable(question.getAnswers()).orElse(Collections.emptyList()).stream()
                        .filter(answer -> answer.getQuestions() != null)
                        .map(answer -> getQuestionById(answer.getQuestions(), id))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
