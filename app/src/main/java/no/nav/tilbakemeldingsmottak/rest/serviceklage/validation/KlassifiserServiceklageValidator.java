package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.model.DefaultAnswers;
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.Question;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.NONE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class KlassifiserServiceklageValidator extends RequestValidator {

    public void validateRequest(KlassifiserServiceklageRequest request, HentSkjemaResponse hentSkjemaResponse) {
        ObjectMapper m = new ObjectMapper();

        Map<String, String> answersMap = m.convertValue(request, new TypeReference<Map<String, String>>() {
                })
                .entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        validateQuestions(hentSkjemaResponse.getQuestions(), answersMap);
        if (hentSkjemaResponse.getDefaultAnswers() != null) {
            validateDefaultAnswers(hentSkjemaResponse.getDefaultAnswers(), answersMap);
        }
    }

    private void validateDefaultAnswers(DefaultAnswers defaultAnswers, Map<String, String> answersMap) {
        defaultAnswers.getAnswers().forEach((questionId, defaultAnswer) ->
                validateDefaultAnswer(questionId, defaultAnswer, answersMap.get(questionId)));
    }

    private boolean validateQuestions(List<Question> questions, Map<String, String> answersMap) {
        for (Question question : questions) {
            String answered = answersMap.get(question.getId());
            switch (question.getType()) {
                case RADIO:
                case SELECT:
                case DATALIST:
                    validateMultichoice(question, answered);
                    break;
                case TEXT:
                case INPUT:
                    validateText(question, answered);
                    break;
                case DATE:
                    validateDate(question, answered);
                    break;
                case CHECKBOX:
                    if (answered != null) {
                        Arrays.stream(answered.split(",")).forEach(a -> validateMultichoice(question, a));
                    }
                    break;
            }
            if (isFinalQuestion(question, answered)) {
                return false;
            }

            // Valider sub-spørsmål rekursivt
            boolean shouldContinue = Optional.ofNullable(question.getAnswers()).orElse(Collections.emptyList())
                    .stream()
                    .filter(a -> a.getAnswer().equals(answered))
                    .findFirst()
                    .map(chosenAnswer ->
                            chosenAnswer.getQuestions() == null || validateQuestions(chosenAnswer.getQuestions(), answersMap))
                    .orElse(true);

            if (!shouldContinue) {
                break;
            }
        }
        return true;
    }

    private void validateMultichoice(Question question, String answer) {
        if (question.getAnswers().stream().noneMatch(a -> a.getAnswer().equals(answer))) {
            throw new ClientErrorException(String.format("Innsendt svar på spørsmål med id=%s er ikke gyldig", question.getId()));
        }
    }

    private void validateText(Question question, String answer) {
        if (isBlank(answer)) {
            throw new ClientErrorException(String.format("Innsendt svar på spørsmål med id=%s er ikke gyldig", question.getId()));
        }
    }

    private void validateDate(Question question, String answer) {
        validateText(question, answer);
        try {
            LocalDate.parse(answer);
        } catch (DateTimeParseException e) {
            throw new ClientErrorException(String.format("Innsendt svar på spørsmål med id=%s er ikke gyldig", question.getId()));
        }
    }

    private void validateDefaultAnswer(String id, String defaultAnswer, String submittedAnswer) {
        if (!defaultAnswer.equals(submittedAnswer)) {
            throw new ClientErrorException(String.format("Innsendt svar på spørsmål med id=%s matcher ikke svar i database", id));
        }
    }

    private boolean isFinalQuestion(Question question, String answered) {
        return NONE.equals(question.getNext()) ||
                (question.getType() != Question.TypeEnum.CHECKBOX &&
                        question.getAnswers() != null &&
                        NONE.equals(question.getAnswers().stream()
                                .filter(a -> a.getAnswer().equals(answered))
                                .findFirst()
                                .orElseThrow(() -> new ClientErrorException(String.format("Innsendt svar på spørsmål med id=%s er ikke gyldig", question.getId())))
                                .getNext()));
    }
}
