package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.Question

class SkjemaUtils {

    companion object {
        @JvmStatic
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
    }


}
