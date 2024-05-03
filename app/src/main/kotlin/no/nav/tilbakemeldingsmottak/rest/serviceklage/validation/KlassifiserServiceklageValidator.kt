package no.nav.tilbakemeldingsmottak.rest.serviceklage.validation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.NONE
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.DefaultAnswers
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.Question
import no.nav.tilbakemeldingsmottak.model.QuestionType.*
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator
import org.apache.commons.lang3.StringUtils.isBlank
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Component
class KlassifiserServiceklageValidator : RequestValidator() {

    fun validateRequest(request: KlassifiserServiceklageRequest, hentSkjemaResponse: HentSkjemaResponse) {
        val mapper = ObjectMapper().registerKotlinModule()

        val answersMap: Map<String, String> =
            mapper.convertValue<Map<String, String?>>(request).filterValues { it != null }
                .mapValues { it.value.toString() }

        hentSkjemaResponse.questions?.let { validateQuestions(it, answersMap) }
        hentSkjemaResponse.defaultAnswers?.let {
            validateDefaultAnswers(it, answersMap)
        }
    }

    private fun validateDefaultAnswers(defaultAnswers: DefaultAnswers, answersMap: Map<String, String>) {
        defaultAnswers.answers?.forEach { (questionId, defaultAnswer) ->
            validateDefaultAnswer(questionId, defaultAnswer, answersMap[questionId])
        }
    }

    private fun validateQuestions(questions: List<Question>, answersMap: Map<String, String>): Boolean {
        for (question in questions) {
            val answered = answersMap[question.id]
            when (question.type) {
                RADIO, SELECT, DATALIST -> validateMultichoice(
                    question,
                    answered
                )

                TEXT, INPUT -> validateText(question, answered)
                DATE -> validateDate(question, answered)
                CHECKBOX -> {
                    answered?.split(",")?.forEach { validateMultichoice(question, it) }
                }

                null -> throw ClientErrorException("Spørsmål med id=${question.id} har ikke type")
            }

            if (isFinalQuestion(question, answered)) {
                return false
            }

            val shouldContinue = question.answers.orEmpty()
                .firstOrNull { it.answer == answered }
                ?.let { chosenAnswer ->
                    chosenAnswer.questions == null || validateQuestions(
                        chosenAnswer.questions!!,
                        answersMap
                    )
                }
                ?: true

            if (!shouldContinue) {
                break
            }
        }

        return true
    }

    private fun validateMultichoice(question: Question, answer: String?) {
        if (question.answers?.none { it.answer == answer } == true) {
            throw ClientErrorException("Innsendt svar på spørsmål med id=${question.id} er ikke gyldig")
        }
    }

    private fun validateText(question: Question, answer: String?) {
        if (isBlank(answer)) {
            throw ClientErrorException("Innsendt svar på spørsmål med id=${question.id} er ikke gyldig")
        }
    }

    private fun validateDate(question: Question, answer: String?) {
        validateText(question, answer)
        try {
            LocalDate.parse(answer)
        } catch (e: DateTimeParseException) {
            throw ClientErrorException("Innsendt svar på spørsmål med id=${question.id} er ikke gyldig")
        } catch (e: Exception) {
            throw ClientErrorException("Innsendt svar på spørsmål med id=${question.id} er ikke gyldig")
        }
    }

    private fun validateDefaultAnswer(id: String, defaultAnswer: String, submittedAnswer: String?) {
        if (defaultAnswer != submittedAnswer) {
            throw ClientErrorException("Innsendt svar på spørsmål med id=$id matcher ikke svar i database")
        }
    }

    private fun isFinalQuestion(question: Question, answered: String?): Boolean {
        // Check if there's no next question
        if (question.next == NONE) return true

        // For non-checkbox questions with answers
        if (question.type != CHECKBOX && question.answers != null) {
            val matchedAnswer = question.answers
                ?.find { it.answer == answered }
                ?: throw ClientErrorException("Innsendt svar på spørsmål med id=${question.id} er ikke gyldig")

            if (matchedAnswer.next == NONE) return true
        }

        return false
    }


}
