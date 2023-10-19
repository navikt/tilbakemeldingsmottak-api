package no.nav.tilbakemeldingsmottak.itest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.FREMMET_DATO
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.INNSENDER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.KOMMUNAL_KLAGE
import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER
import no.nav.tilbakemeldingsmottak.domain.enums.HendelseType
import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageResponse
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.Klagetyper
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAv
import no.nav.tilbakemeldingsmottak.rest.common.domain.ErrorResponse
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_1
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_ENHETSNR_2
import no.nav.tilbakemeldingsmottak.util.builders.InnmelderBuilder
import no.nav.tilbakemeldingsmottak.util.builders.KlassifiserServiceklageRequestBuilder
import no.nav.tilbakemeldingsmottak.util.builders.OpprettServiceklageRequestBuilder
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.transaction.TestTransaction

internal class ServiceklageIT : ApplicationTest() {
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val SAKSBEHANDLER = "Saksbehandler"
    private val ORGANISASJONSNUMMER = "243602076"
    private val KLAGETEKST = "Dette er en klage"
    private val PAAVEGNEAV_PERSONNUMMER = "28898698736"
    private val URL_SENDINN_SERVICEKLAGE = "/rest/serviceklage"
    private val URL_BEHANDLE_SERVICEKLAGE = "/rest/taskserviceklage"
    private val KLASSIFISER = "klassifiser"
    private val HENT_DOKUMENT = "hentdokument"
    private val JOURNALPOST_ID = "12345"
    private val OPPGAVE_ID = "1234567"
    private val GJELDER = "Gjelder én ytelse eller tjeneste"
    private val BESKRIVELSE = "Bruker klager på service"
    private val YTELSE = "AAP - Arbeidsavklaringspenger"
    private val RELATERT = "EØS-saken,Åpningstider på NAV-kontoret"
    private val TEMA = "Vente på NAV"
    private val VENTE = "Saksbehandlingstid"
    private val UTFALL = "b) Regler/rutiner/frister er fulgt men NAV burde ivaretatt bruker bedre"
    private val AARSAK = "Service har vært dårlig"
    private val TILTAK = "Gi bedre service"
    private val INNSENDER_ANSWER = "Bruker selv som privatperson"
    private val BEHANDLES_SOM_SERVICEKLAGE_ANSWER = "Ja"

    private fun assertBasicServiceklageFields(serviceklage: Serviceklage) {
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertNotNull(serviceklage.fremmetDato)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)

        if (serviceklage.klagetekst != null) {
            assertEquals(KLAGETEKST, serviceklage.klagetekst)
        }
    }

    @Test
    fun `Should return correct data as a private person`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val personnummer = msg.innmelder!!.personnummer!!

        val requestEntity = HttpEntity(msg, createHeaders(Constants.TOKENX_ISSUER, personnummer, true))
        val response = api?.createServiceklage(requestEntity)

        // When
        assertEquals(HttpStatus.OK, response?.statusCode)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().first()

        assertBasicServiceklageFields(serviceklage)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertTrue(serviceklage.innlogget!!)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }


    @Test
    fun `Should return correct data as a private person when not logged in`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val personnummer = msg.innmelder!!.personnummer!!

        // When
        val requestEntity = HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, personnummer, false))
        val response = api?.createServiceklage(requestEntity)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().first()

        assertEquals(HttpStatus.OK, response?.statusCode)

        assertBasicServiceklageFields(serviceklage)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertFalse(serviceklage.innlogget!!)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }


    @Test
    fun `Should return correct data when acting on behalf of another person`() {
        // Given
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build()
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.TOKENX_ISSUER, request.innmelder!!.personnummer!!, true))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response?.statusCode)
        assertBasicServiceklageFields(serviceklage)
        assertEquals(PAAVEGNEAV_PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(PaaVegneAv.ANNEN_PERSON.value, serviceklage.innsender)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }

    @Test
    fun `Should return correct data when acting on behalf of a company`() {
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build()
        val requestEntity = HttpEntity(request, createHeaders())
        val response = api?.createServiceklage(requestEntity)

        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response?.statusCode)
        assertBasicServiceklageFields(serviceklage)
        assertEquals(ORGANISASJONSNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(PaaVegneAv.BEDRIFT.value, serviceklage.innsender)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }

    @Test
    fun `Should return correct data when the person wish to be contacted`() {
        // Given
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(innmelder = InnmelderBuilder().build(telefonnummer = "12345678"), oenskerAaKontaktes = true)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!, true))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(response?.statusCode, HttpStatus.OK)
        assertBasicServiceklageFields(serviceklage)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertNull(serviceklage.svarmetode)
        assertNull(serviceklage.svarmetodeUtdypning)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }

    @Test
    fun `Should return correct data when the person on behalf of someone else is missing power of attorney`() {
        // Given
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
            .build(
                oenskerAaKontaktes = null,
                innmelder = InnmelderBuilder().build(harFullmakt = false, rolle = "Advokat")
            )
        val requestEntity = HttpEntity(request, createHeaders())

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertBasicServiceklageFields(serviceklage)
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(PAAVEGNEAV_PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(PaaVegneAv.ANNEN_PERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(INNMELDER_MANGLER_FULLMAKT_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }

    @Test
    fun `Should return correct data when there is more than one complaint type`() {
        // Given
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(klagetyper = listOf(Klagetyper.BREV, Klagetyper.TELEFON))

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!, true))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response?.statusCode)
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.BREV.value + ", " + Klagetyper.TELEFON.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(OPPGAVE_ID, serviceklage.oppgaveId)
    }

    @Test
    fun `Should return OK even when creating a journalpost fails (reverts to sending email)`() {
        // Given
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/")).willReturn(
                WireMock.aResponse().withStatus(500)
            )
        )
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(0, serviceklageRepository!!.count())
    }

    @Test
    fun `Should return OK even when creating a oppgave fails (reverts to sending email)`() {
        // Given
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPGAVE")).willReturn(WireMock.aResponse().withStatus(500))
        )
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(1, serviceklageRepository!!.count())
    }

    @Test
    fun `Should fail if the complaint text is too long`() {
        // Given
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(klagetekst = RandomStringUtils.randomAlphabetic(50000))
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response?.statusCode)
    }

    @Test
    fun `Should return correct data when classifying serviceklage`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        api?.createServiceklage(requestEntityOpprett)

        assertEquals(serviceklageRepository!!.count(), 1)
        val fremmetDato = serviceklageRepository!!.findAll().iterator().next().fremmetDato.toString()
        val request = KlassifiserServiceklageRequestBuilder().build(FREMMET_DATO = fremmetDato)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        // When
        val response = api?.classifyServiceklage(requestEntity, OPPGAVE_ID)

        // Then
        assertEquals(HttpStatus.OK, response?.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        assertEquals(serviceklageRepository!!.count(), 1)

        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertBasicServiceklageFields(serviceklage)
        assertEquals(BEHANDLES_SOM_SERVICEKLAGE_ANSWER, serviceklage.behandlesSomServiceklage)
        assertEquals(fremmetDato, serviceklage.fremmetDato.toString())
        assertEquals(INNSENDER_ANSWER, serviceklage.innsender)
        assertEquals(NAV_ENHETSNR_1, serviceklage.enhetsnummerPaaklaget)
        assertEquals(NAV_ENHETSNR_2, serviceklage.enhetsnummerBehandlende)
        assertEquals(GJELDER, serviceklage.gjelder)
        assertEquals(BESKRIVELSE, serviceklage.beskrivelse)
        assertEquals(RELATERT, serviceklage.relatert)
        assertEquals(YTELSE, serviceklage.ytelse)
        assertEquals(TEMA, serviceklage.tema)
        assertEquals(VENTE, serviceklage.temaUtdypning)
        assertEquals(UTFALL, serviceklage.utfall)
        assertEquals(AARSAK, serviceklage.aarsak)
        assertEquals(TILTAK, serviceklage.tiltak)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(objectMapper.writeValueAsString(request), serviceklage.klassifiseringJson)
    }

    @Test
    fun `Should delete documents if the complaint is municipal`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        val opprettServiceklageResponse = api?.createServiceklage(requestEntityOpprett)

        val serviceklage = serviceklageRepository!!.findByJournalpostId(
            opprettServiceklageResponse?.body!!.journalpostId!!
        )

        val fremmetDato = serviceklage?.fremmetDato.toString()
        val request = KlassifiserServiceklageRequestBuilder().asKommunalKlage().build(FREMMET_DATO = fremmetDato)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        // When
        val response = api?.classifyServiceklage(requestEntity, opprettServiceklageResponse.body!!.oppgaveId!!)

        // Then
        assertEquals(HttpStatus.OK, response?.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlPathMatching("/OPPGAVE")))
        assertEquals(serviceklageRepository!!.count(), 1)

        val oppdatertServiceklage = serviceklageRepository!!.findAll().iterator().next()

        assertNotNull(opprettServiceklageResponse.body)
        assertEquals(oppdatertServiceklage.behandlesSomServiceklage, KOMMUNAL_KLAGE)
        assertEquals(oppdatertServiceklage.klassifiseringJson, objectMapper.writeValueAsString(request))
    }

    @Test
    fun `Should create a serviceklage if it's missing`() {
        val request: KlassifiserServiceklageRequest = KlassifiserServiceklageRequestBuilder().build()
        val requestEntity = HttpEntity(
            request,
            createHeaders(Constants.AZURE_ISSUER, request.INNSENDER!!, "serviceklage-klassifisering")
        )
        val response = restTemplate!!.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$KLASSIFISER?oppgaveId=$OPPGAVE_ID",
            HttpMethod.PUT,
            requestEntity,
            KlassifiserServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertBasicServiceklageFields(serviceklage)
        assertEquals(BEHANDLES_SOM_SERVICEKLAGE_ANSWER, serviceklage.behandlesSomServiceklage)
        assertEquals(INNSENDER_ANSWER, serviceklage.innsender)
        assertEquals(NAV_ENHETSNR_1, serviceklage.enhetsnummerPaaklaget)
        assertEquals(NAV_ENHETSNR_2, serviceklage.enhetsnummerBehandlende)
        assertEquals(GJELDER, serviceklage.gjelder)
        assertEquals(BESKRIVELSE, serviceklage.beskrivelse)
        assertEquals(RELATERT, serviceklage.relatert)
        assertEquals(YTELSE, serviceklage.ytelse)
        assertEquals(TEMA, serviceklage.tema)
        assertEquals(VENTE, serviceklage.temaUtdypning)
        assertEquals(UTFALL, serviceklage.utfall)
        assertEquals(AARSAK, serviceklage.aarsak)
        assertEquals(TILTAK, serviceklage.tiltak)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
        assertEquals(objectMapper.writeValueAsString(request), serviceklage.klassifiseringJson)
    }

    @Test
    fun `Should return correct data when getting schema`() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        api?.createServiceklage(requestEntityOpprett)

        assertEquals(1, serviceklageRepository!!.count())
        val fremmetDato = serviceklageRepository!!.findAll().iterator().next().fremmetDato.toString()

        val requestEntity =
            HttpEntity<Any?>(createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        val response = api?.getSkjema(requestEntity, JOURNALPOST_ID)
        assertEquals(HttpStatus.OK, response?.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        val hentSkjemaResponse = response?.body
        assertNotNull(hentSkjemaResponse)
        assertEquals(
            hentSkjemaResponse!!.defaultAnswers!!.answers!![FREMMET_DATO],
            fremmetDato
        )
        assertEquals(
            hentSkjemaResponse.defaultAnswers!!.answers!![INNSENDER],
            INNSENDER_ANSWER
        )
        assertEquals(
            hentSkjemaResponse.defaultAnswers!!.answers!![ServiceklageConstants.KANAL],
            KANAL_SERVICEKLAGESKJEMA_ANSWER
        )
        assertEquals(
            hentSkjemaResponse.defaultAnswers!!.answers!![ServiceklageConstants.SVARMETODE],
            SVAR_IKKE_NOEDVENDIG_ANSWER
        )
        assertEquals(
            hentSkjemaResponse.defaultAnswers!!.answers!![ServiceklageConstants.SVAR_IKKE_NOEDVENDIG],
            BRUKER_IKKE_BEDT_OM_SVAR_ANSWER
        )
    }

    @Test
    fun `Should return correct data when getting document`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        val opprettResponse = api?.createServiceklage(requestEntityOpprett)

        assertEquals(serviceklageRepository!!.count(), 1)
        assertNotNull(opprettResponse?.body)
        val requestEntity =
            HttpEntity<Any?>(createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        // When
        val response = api?.getDocument(requestEntity, opprettResponse?.body!!.oppgaveId!!)

        // Then
        assertEquals(HttpStatus.OK, response?.statusCode)
    }

    @Test
    fun `Should return 204 when there is no document`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val opprettRequestEntity =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        api?.createServiceklage(opprettRequestEntity)

        val requestEntity =
            HttpEntity<Any?>(createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        // When
        val response = api?.getDocument(requestEntity, "99")

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response?.statusCode)
    }

    @Test
    fun `Should return 404 when ereg is not found`() {
        // Given
        val request = OpprettServiceklageRequestBuilder().asBedrift().build()
        val requestEntity = HttpEntity<Any>(request, createHeaders())
        WireMock.setScenarioState("opprett_serviceklage", "ereg_404")

        // When
        val response =
            restTemplate!!.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, ErrorResponse::class.java)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body!!.errorCode)
        assertEquals(ErrorCode.EREG_NOT_FOUND.value, response.body!!.errorCode)
    }

    @Test
    fun `Should return 403 when missing access to Joark Document`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.TOKENX_ISSUER, msg.innmelder!!.personnummer!!))

        val opprettResponse = api?.createServiceklage(requestEntityOpprett)

        WireMock.setScenarioState("hent_dokument", "saf_403")

        // When
        val response: ResponseEntity<ErrorResponse>? = restTemplate!!.exchange(
            URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + opprettResponse?.body!!.oppgaveId,
            HttpMethod.GET,
            HttpEntity<Any?>(createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering")),
            ErrorResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response?.statusCode)
        assertNotNull(response?.body)
        assertNotNull(response?.body?.errorCode)
        assertEquals(ErrorCode.SAF_FORBIDDEN.value, response?.body?.errorCode)
    }

    @Test
    fun `Should add hendelse when creating a serviceklage`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val personnummer = msg.innmelder!!.personnummer!!

        val requestEntity = HttpEntity(msg, createHeaders(Constants.TOKENX_ISSUER, personnummer, true))

        // When
        val response = api?.createServiceklage(requestEntity)

        // Then
        val hendelser = hendelseRepository?.findAllByJournalpostId(response!!.body!!.journalpostId!!)
        val hendelse = hendelser?.get(0)
        assertEquals(HendelseType.OPPRETT_SERVICEKLAGE.name, hendelse?.hendelsetype)
        assertEquals(JOURNALPOST_ID, hendelse?.journalpostId)
    }

    @Test
    fun `Should add hendelse when classifying a serviceklage`() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntityOpprett =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!))
        val opprettResponse = api?.createServiceklage(requestEntityOpprett)

        val fremmetDato = serviceklageRepository!!.findAll().iterator().next().fremmetDato.toString()
        val request = KlassifiserServiceklageRequestBuilder().build(FREMMET_DATO = fremmetDato)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))

        // When
        api?.classifyServiceklage(requestEntity, OPPGAVE_ID)

        // Then
        val hendelser = hendelseRepository?.findAllByJournalpostId(opprettResponse!!.body!!.journalpostId!!)
        val createdHendelse = hendelser?.find { it.hendelsetype == HendelseType.OPPRETT_SERVICEKLAGE.name }
        val classifiedHendelse = hendelser?.find { it.hendelsetype == HendelseType.KLASSIFISER_SERVICEKLAGE.name }

        assertEquals(HendelseType.OPPRETT_SERVICEKLAGE.name, createdHendelse?.hendelsetype)
        assertEquals(JOURNALPOST_ID, createdHendelse?.journalpostId)

        assertEquals(HendelseType.KLASSIFISER_SERVICEKLAGE.name, classifiedHendelse?.hendelsetype)
        assertEquals(JOURNALPOST_ID, classifiedHendelse?.journalpostId)
    }

}
