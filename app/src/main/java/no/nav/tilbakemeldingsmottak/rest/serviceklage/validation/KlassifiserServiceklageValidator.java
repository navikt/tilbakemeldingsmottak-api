package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.NONE;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.DefaultAnswers;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Question;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class KlassifiserServiceklageValidator extends RequestValidator {


    public void validateRequest(KlassifiserServiceklageRequest request, HentSkjemaResponse hentSkjemaResponse) {
        ObjectMapper m = new ObjectMapper();
        Map<String, String> answersMap = m.convertValue(request.getAnswers(), Map.class);

        validateQuestions(hentSkjemaResponse.getQuestions(), answersMap);
        if (hentSkjemaResponse.getDefaultAnswers() != null) {
            validateDefaultAnswers(hentSkjemaResponse.getDefaultAnswers(), answersMap);
        }
    }

    private void validateDefaultAnswers(DefaultAnswers defaultAnswers, Map<String, String> answersMap) {
        defaultAnswers.getAnswers().forEach((questionId, defaultAnswer) ->
                validateDefaultAnswer(questionId, defaultAnswer, answersMap.get(questionId)));
    }

    private void validateQuestions(List<Question> questions, Map<String, String> answersMap) {
        for (Question question : questions) {
            String answered = answersMap.get(question.getId());
            switch (question.getType()) {
                case RADIO:
                     validateRadio(question, answersMap.get(question.getId()));
                    break;
                case TEXT:
                    validateText(question, answersMap.get(question.getId()));
                    break;
                case INPUT:
                    validateInput(question, answersMap.get(question.getId()));
                    break;
                case DATE:
                    validateDate(question, answersMap.get(question.getId()));
                    break;
                case SELECT:
                    validateSelect(question, answersMap.get(question.getId()));
                    break;
                case DATALIST:
                    validateDatalist(question, answersMap.get(question.getId()));
                    break;
            }
            if (NONE.equals(question.getNext())) {
                return;
            }

            Optional.ofNullable(question.getAnswers()).orElse(Collections.emptyList())
                    .stream()
                    .filter(a -> a.getAnswer().equals(answered))
                    .findFirst()
                    .ifPresent(chosenAnswer -> {
                        List<Question> subQuestions = chosenAnswer.getQuestions();
                        if (subQuestions != null) {
                            validateQuestions(subQuestions, answersMap);
                        }
                    });

        }
    }

    private void validateRadio(Question question, String answer) {
        if (question.getAnswers().stream().noneMatch(a -> a.getAnswer().equals(answer))) {
            throw new InvalidRequestException(String.format("Innsendt svar på spørsmål med id=%s er ikke gyldig %s %s %s %s", question.getId(),
                    answer, question.getAnswers().get(0), question.getAnswers().get(1), question.getAnswers().get(2)));
        }
    }

    private void validateText(Question question, String answer) {
    }

    private void validateInput(Question question, String answer) {
    }

    private void validateDate(Question question, String answer) {
    }

    private void validateSelect(Question question, String answer) {
    }

    private void validateDatalist(Question question, String answer) {
    }

    private void validateDefaultAnswer(String id, String defaultAnswer, String submittedAnswer) {
        if (!defaultAnswer.equals(submittedAnswer)) {
            throw new InvalidRequestException(String.format("Innsendt svar på spørsmål med id=%s matcher ikke svar i database", id));
        }
    }

}
