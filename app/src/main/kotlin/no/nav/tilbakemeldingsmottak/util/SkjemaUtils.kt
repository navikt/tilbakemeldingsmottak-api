package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.Question

class SkjemaUtils {

    companion object {
        // OBS: Hvis flere spørsmål har samme id, returneres kun det første
        fun getQuestionById(questions: List<Question>, id: String): Question? {
            return questions.asSequence()
                .mapNotNull { question ->
                    when {
                        id == question.id -> question
                        else -> question.answers?.firstNotNullOfOrNull { answer ->
                            getQuestionById(answer.questions ?: emptyList(), id)
                        }
                    }
                }
                .firstOrNull()
        }

        // Det samme spørsmålet kan være duplisert inn i flere svargrener, så alle forekomster
        // av id-en oppdateres - ikke bare den første.
        fun updateQuestionsById(
            questions: List<Question>?,
            id: String,
            update: (Question) -> Question
        ): List<Question> {
            return questions.orEmpty().map { question ->
                val withUpdatedSubQuestions = question.answers?.let { answers ->
                    question.copy(answers = answers.map { answer ->
                        answer.questions?.let { answer.copy(questions = updateQuestionsById(it, id, update)) }
                            ?: answer
                    })
                } ?: question

                if (withUpdatedSubQuestions.id == id) update(withUpdatedSubQuestions) else withUpdatedSubQuestions
            }
        }
    }


}
