package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.model.Answer
import no.nav.tilbakemeldingsmottak.model.Question
import no.nav.tilbakemeldingsmottak.model.QuestionType
import no.nav.tilbakemeldingsmottak.util.SkjemaUtils.Companion.getQuestionById
import no.nav.tilbakemeldingsmottak.util.SkjemaUtils.Companion.updateQuestionsById
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SkjemaUtilsTest {

    private val enhetsnummer = Question(id = "ENHETSNUMMER", text = "Angi enhetsnummer", type = QuestionType.DATALIST)

    // Samme spørsmål er duplisert inn i to svargrener, slik det er i schema.yaml
    private val questions = listOf(
        Question(
            id = "BEHANDLES",
            text = "Skal klagen behandles?",
            type = QuestionType.RADIO,
            answers = listOf(
                Answer(answer = "Ja", questions = listOf(enhetsnummer)),
                Answer(answer = "Nei", questions = listOf(enhetsnummer))
            )
        ),
        enhetsnummer
    )

    @Test
    fun oppdatererAlleForekomsterAvSammeId() {
        val enheter = listOf(Answer(answer = "Nav Sarpsborg - 0105"))

        val updated = updateQuestionsById(questions, "ENHETSNUMMER") { it.copy(answers = enheter) }

        val forekomster = listOf(
            updated[0].answers!![0].questions!![0],
            updated[0].answers!![1].questions!![0],
            updated[1]
        )
        forekomster.forEach { assertEquals(enheter, it.answers) }
    }

    @Test
    fun larAndreSpoersmaalVaereUroert() {
        val updated = updateQuestionsById(questions, "ENHETSNUMMER") { it.copy(answers = emptyList()) }

        assertEquals(questions[0].id, updated[0].id)
        assertEquals("Ja", updated[0].answers!![0].answer)
        assertEquals("Nei", updated[0].answers!![1].answer)
    }

    @Test
    fun getQuestionByIdFinnerSpoersmaalIEnSvargren() {
        assertEquals(enhetsnummer, getQuestionById(questions, "ENHETSNUMMER"))
    }
}
