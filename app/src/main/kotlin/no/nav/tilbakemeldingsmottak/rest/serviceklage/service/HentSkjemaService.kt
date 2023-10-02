package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tilbakemeldingsmottak.consumer.norg2.Norg2Consumer
import no.nav.tilbakemeldingsmottak.domain.Serviceklage
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.FREMMET_DATO
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.INNSENDER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVARMETODE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVARMETODE_UTDYPNING
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import no.nav.tilbakemeldingsmottak.model.Answer
import no.nav.tilbakemeldingsmottak.model.DefaultAnswers
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.model.Question
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import no.nav.tilbakemeldingsmottak.util.SkjemaUtils.Companion.getQuestionById
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@Service
class HentSkjemaService(
    @Value(SCHEMA_PATH) schema: Resource,
    private val serviceklageRepository: ServiceklageRepository,
    private val norg2Consumer: Norg2Consumer
) {
    companion object {
        private const val SCHEMA_PATH = "classpath:schema/schema.yaml"
    }

    private var classpathSkjema: String = ""
    private val MESSAGE = "Feltet ble fylt ut under registrering av serviceklage"
    private val NEDLAGT = "nedlagt"
    private val ANNET = "Annet"
    private val CHARSET = StandardCharsets.UTF_8

    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule().apply {
        findAndRegisterModules()
    }

    init {
        classpathSkjema = StreamUtils.copyToString(schema.inputStream, CHARSET)
    }

    // Get questions/answers from schema.yaml and update the answers with "enheter" from norg2 og default answers from "serviceklage"
    fun hentSkjema(journalpostId: String): HentSkjemaResponse {
        var response = readSkjema()
        val enheter = hentEnheter()

        val updatedQuestionPaaklaget =
            response.questions?.let { getQuestionById(it, ENHETSNUMMER_PAAKLAGET)?.copy(answers = enheter) }
                ?: throw ServerErrorException("Finner ikke spørsmål med id=$ENHETSNUMMER_PAAKLAGET")
        response = updateQuestionInResponse(response, updatedQuestionPaaklaget)

        val updatedQuestionBehandlede =
            response.questions?.let { getQuestionById(it, ENHETSNUMMER_BEHANDLENDE)?.copy(answers = enheter) }
                ?: throw ServerErrorException("Finner ikke spørsmål med id=$ENHETSNUMMER_BEHANDLENDE")
        response = updateQuestionInResponse(response, updatedQuestionBehandlede)

        serviceklageRepository.findByJournalpostId(journalpostId)?.let { serviceklage ->
            response = response.copy(
                defaultAnswers = DefaultAnswers(
                    message = MESSAGE,
                    answers = mapDefaultAnswers(serviceklage)
                )
            )
        }

        return response
    }

    fun updateQuestionInResponse(response: HentSkjemaResponse, updatedQuestion: Question): HentSkjemaResponse {
        val updatedQuestions = updateQuestionInResponseRecursively(response.questions, updatedQuestion)
        return response.copy(questions = updatedQuestions)
    }

    fun updateQuestionInResponseRecursively(questions: List<Question>?, updatedQuestion: Question): List<Question> {
        return questions?.map { question ->
            if (question.id == updatedQuestion.id) {
                // If the current question matches the updated question, replace it
                updatedQuestion
            } else {
                // If it's not the updated question, check if it has answers with nested questions
                val updatedAnswers = question.answers?.map { answer ->
                    val updatedQuestionsInAnswer =
                        updateQuestionInResponseRecursively(answer.questions, updatedQuestion)
                    answer.copy(questions = updatedQuestionsInAnswer)
                }
                question.copy(answers = updatedAnswers)
            }
        } ?: emptyList()
    }


    fun readSkjema(): HentSkjemaResponse {
        return try {
            mapper.readValue(classpathSkjema, HentSkjemaResponse::class.java)
        } catch (e: Exception) {
            throw ServerErrorException("Feil under serialisering av skjema", e)
        }
    }

    private fun mapDefaultAnswers(serviceklage: Serviceklage): Map<String, String> {
        return hashMapOf<String, String>().apply {
            serviceklage.fremmetDato?.let { put(FREMMET_DATO, it.toString()) }
            serviceklage.innsender?.let { put(INNSENDER, it) }
            serviceklage.kanal?.let { put(KANAL, it) }
            serviceklage.svarmetode?.let { put(SVARMETODE, it) }
            serviceklage.svarmetodeUtdypning?.let {
                when (it) {
                    BRUKER_IKKE_BEDT_OM_SVAR_ANSWER -> put(SVAR_IKKE_NOEDVENDIG, it)
                    else -> {
                        put(SVAR_IKKE_NOEDVENDIG, ANNET)
                        put(SVARMETODE_UTDYPNING, it)
                    }
                }
            }
        }
    }

    private fun hentEnheter(): List<Answer> {
        val enheter = norg2Consumer.hentEnheter()
        return enheter
            .filter { !it.status.equals(NEDLAGT, ignoreCase = true) }
            .map { Answer(answer = "${it.navn} - ${it.enhetNr}") }
    }
}
