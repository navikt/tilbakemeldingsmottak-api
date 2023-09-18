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
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.model.*
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
import java.time.LocalDate

internal class ServiceklageIT : ApplicationTest() {
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val SAKSBEHANDLER = "Saksbehandler"
    private val ORGANISASJONSNUMMER = "243602076"
    private val KLAGETEKST = "Dette er en klage"
    private val PAAVEGNEAV_PERSONNUMMER = "28898698736"
    private val URL_SENDINN_SERVICEKLAGE = "/rest/serviceklage"
    private val URL_BEHANDLE_SERVICEKLAGE = "/rest/taskserviceklage"
    private val KLASSIFISER = "klassifiser"
    private val HENT_SKJEMA = "hentskjema"
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
    private val FREMMET_DATO_ANSWER = LocalDate.now().toString()

    @Test
    fun happyPathPrivatperson() {
        val msg: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntity =
            HttpEntity(msg, createHeaders(Constants.TOKENX_ISSUER, msg.innmelder!!.personnummer!!, true))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()
        assertEquals(HttpStatus.OK, response.statusCode)

        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertTrue(serviceklage.innlogget!!)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
    }

    @Test
    fun happyPathPrivatpersonIkkePaLogget() {
        val msg: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val requestEntity =
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!, false))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertFalse(serviceklage.innlogget!!)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)

    }

    @Test
    fun happyPathAnnenPerson() {
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build()
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.TOKENX_ISSUER, request.innmelder!!.personnummer!!, true))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PAAVEGNEAV_PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertEquals(PaaVegneAv.ANNEN_PERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)
    }

    @Test
    fun happyPathBedrift() {
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build()
        val requestEntity = HttpEntity(request, createHeaders())
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(ORGANISASJONSNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertEquals(PaaVegneAv.BEDRIFT.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER, serviceklage.svarmetodeUtdypning)

    }

    @Test
    fun happyPathOenskerAaKontaktes() {
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(innmelder = InnmelderBuilder().build(telefonnummer = "12345678"), oenskerAaKontaktes = true)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!, true))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(response.statusCode, HttpStatus.OK)
        assertNotNull(serviceklage.serviceklageId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PaaVegneAv.PRIVATPERSON.value, serviceklage.innsender)
        assertEquals(PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNull(serviceklage.svarmetode)
        assertNull(serviceklage.svarmetodeUtdypning)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)

    }

    @Test
    fun happyPathInnsenderManglerFullmakt() {
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
            .build(
                oenskerAaKontaktes = null,
                innmelder = InnmelderBuilder().build(harFullmakt = false, rolle = "Advokat")
            )
        val requestEntity = HttpEntity(request, createHeaders())
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(serviceklage.serviceklageId)
        assertEquals(JOURNALPOST_ID, serviceklage.journalpostId)
        assertNotNull(serviceklage.opprettetDato)
        assertEquals(PAAVEGNEAV_PERSONNUMMER, serviceklage.klagenGjelderId)
        assertEquals(Klagetyper.NAV_DIGITALE_TJENESTER.value, serviceklage.klagetyper)
        assertEquals(KLAGETEKST, serviceklage.klagetekst)
        assertNotNull(serviceklage.fremmetDato)
        assertEquals(PaaVegneAv.ANNEN_PERSON.value, serviceklage.innsender)
        assertEquals(KANAL_SERVICEKLAGESKJEMA_ANSWER, serviceklage.kanal)
        assertEquals(SVAR_IKKE_NOEDVENDIG_ANSWER, serviceklage.svarmetode)
        assertEquals(INNMELDER_MANGLER_FULLMAKT_ANSWER, serviceklage.svarmetodeUtdypning)
    }

    @Test
    fun happyPathFlereKlagetyper() {
        val request: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(klagetyper = listOf(Klagetyper.BREV, Klagetyper.TELEFON))

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!, true))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        val serviceklage = serviceklageRepository!!.findAll().iterator().next()

        assertEquals(HttpStatus.OK, response.statusCode)
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

    }

    @Test
    fun happyPathOpprettJournalpostFeil() {
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/")).willReturn(
                WireMock.aResponse().withStatus(500)
            )
        )
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, serviceklageRepository!!.count())
    }

    @Test
    fun happyPathOpprettOppgaveFeil() {
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPGAVE")).willReturn(WireMock.aResponse().withStatus(500))
        )
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, serviceklageRepository!!.count())
    }

    @Test
    fun shouldFailIfKlagetekstTooLarge() {
        val request = OpprettServiceklageRequestBuilder().asPrivatPerson()
            .build(klagetekst = RandomStringUtils.randomAlphabetic(50000))
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, request.innmelder!!.personnummer!!))
        val response = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            requestEntity,
            OpprettServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun happyPathKlassifiserServiceklage() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!)),
            OpprettServiceklageResponse::class.java
        )
        assertEquals(serviceklageRepository!!.count(), 1)
        val fremmetDato = serviceklageRepository!!.findAll().iterator().next().fremmetDato.toString()
        val request: KlassifiserServiceklageRequest =
            KlassifiserServiceklageRequestBuilder().build(FREMMET_DATO = fremmetDato)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))
        val response = restTemplate!!.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$KLASSIFISER?oppgaveId=$OPPGAVE_ID",
            HttpMethod.PUT,
            requestEntity,
            KlassifiserServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        assertEquals(serviceklageRepository!!.count(), 1)
        val (_, _, _, _, _, _, _, _, _, behandlesSomServiceklage, _, fremmetDato1, innsender, kanal, _, enhetsnummerPaaklaget, enhetsnummerBehandlende, gjelder, beskrivelse, ytelse, relatert, tema, temaUtdypning, utfall, aarsak, tiltak, svarmetode, svarmetodeUtdypning, _, _, _, klassifiseringJson) = serviceklageRepository!!.findAll()
            .iterator().next()
        assertEquals(behandlesSomServiceklage, BEHANDLES_SOM_SERVICEKLAGE_ANSWER)
        assertEquals(fremmetDato1.toString(), fremmetDato)
        assertEquals(innsender, INNSENDER_ANSWER)
        assertEquals(kanal, KANAL_SERVICEKLAGESKJEMA_ANSWER)
        assertEquals(enhetsnummerPaaklaget, NAV_ENHETSNR_1)
        assertEquals(enhetsnummerBehandlende, NAV_ENHETSNR_2)
        assertEquals(gjelder, GJELDER)
        assertEquals(beskrivelse, BESKRIVELSE)
        assertEquals(relatert, RELATERT)
        assertEquals(ytelse, YTELSE)
        assertEquals(tema, TEMA)
        assertEquals(temaUtdypning, VENTE)
        assertEquals(utfall, UTFALL)
        assertEquals(aarsak, AARSAK)
        assertEquals(tiltak, TILTAK)
        assertEquals(svarmetode, SVAR_IKKE_NOEDVENDIG_ANSWER)
        assertEquals(svarmetodeUtdypning, BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
        assertEquals(klassifiseringJson, objectMapper.writeValueAsString(request))
    }

    @Test
    fun dersomDetErKommunaltSkalDokumenterSlettes() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val opprettServiceklageResponse = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, HttpEntity(
                msg, createHeaders(
                    Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!
                )
            ), OpprettServiceklageResponse::class.java
        )
        assertNotNull(opprettServiceklageResponse.body)
        val serviceklage = serviceklageRepository!!.findByJournalpostId(
            opprettServiceklageResponse.body!!.journalpostId!!
        )

        val fremmetDato = serviceklage?.fremmetDato.toString()
        val request = KlassifiserServiceklageRequestBuilder().asKommunalKlage().build(FREMMET_DATO = fremmetDato)
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"))
        val response = restTemplate!!.exchange(
            URL_BEHANDLE_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + opprettServiceklageResponse.body!!.oppgaveId,
            HttpMethod.PUT,
            requestEntity,
            KlassifiserServiceklageResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlPathMatching("/OPPGAVE")))
        assertEquals(serviceklageRepository!!.count(), 1)
        val (_, _, _, _, _, _, _, _, _, behandlesSomServiceklage, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, klassifiseringJson) = serviceklageRepository!!.findAll()
            .iterator().next()
        assertEquals(behandlesSomServiceklage, KOMMUNAL_KLAGE)
        assertEquals(klassifiseringJson, objectMapper.writeValueAsString(request))
    }

    @Test
    fun enServiceklageSkalOpprettesOmDenMangler() {
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
        val (serviceklageId, journalpostId, opprettetDato, _, _, klagetyper, _, _, klagetekst, behandlesSomServiceklage, _, fremmetDato, innsender, kanal, _, enhetsnummerPaaklaget, enhetsnummerBehandlende, gjelder, beskrivelse, ytelse, _, tema, temaUtdypning, utfall, aarsak, tiltak, svarmetode, svarmetodeUtdypning, _, _, _, klassifiseringJson) = serviceklageRepository!!.findAll()
            .iterator().next()
        assertNotNull(serviceklageId)
        assertNotNull(opprettetDato)
        assertNull(klagetyper)
        assertNull(klagetekst)
        assertEquals(journalpostId, JOURNALPOST_ID)
        assertEquals(behandlesSomServiceklage, BEHANDLES_SOM_SERVICEKLAGE_ANSWER)
        assertEquals(fremmetDato.toString(), FREMMET_DATO_ANSWER)
        assertEquals(innsender, INNSENDER_ANSWER)
        assertEquals(kanal, KANAL_SERVICEKLAGESKJEMA_ANSWER)
        assertEquals(enhetsnummerPaaklaget, NAV_ENHETSNR_1)
        assertEquals(enhetsnummerBehandlende, NAV_ENHETSNR_2)
        assertEquals(gjelder, GJELDER)
        assertEquals(beskrivelse, BESKRIVELSE)
        assertEquals(ytelse, YTELSE)
        assertEquals(tema, TEMA)
        assertEquals(temaUtdypning, VENTE)
        assertEquals(utfall, UTFALL)
        assertEquals(aarsak, AARSAK)
        assertEquals(tiltak, TILTAK)
        assertEquals(svarmetode, SVAR_IKKE_NOEDVENDIG_ANSWER)
        assertEquals(svarmetodeUtdypning, BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
        assertEquals(klassifiseringJson, objectMapper.writeValueAsString(request))
    }

    @Test
    fun happyPathHentSkjema() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!)),
            OpprettServiceklageResponse::class.java
        )
        assertEquals(1, serviceklageRepository!!.count())
        val fremmetDato = serviceklageRepository!!.findAll().iterator().next().fremmetDato.toString()

        val response = restTemplate!!.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$HENT_SKJEMA/$JOURNALPOST_ID", HttpMethod.GET, HttpEntity<Any?>(
                createHeaders(
                    Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"
                )
            ), HentSkjemaResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        val hentSkjemaResponse = response.body
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
    fun happyPathHentDokument() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val opprettResponse = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, HttpEntity(
                msg, createHeaders(
                    Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!
                )
            ), OpprettServiceklageResponse::class.java
        )
        assertEquals(serviceklageRepository!!.count(), 1)
        val (_, _, _, _, _, _, _, _, _, _, _, fremmetDato1) = serviceklageRepository!!.findAll().iterator().next()
        assertNotNull(opprettResponse.body)
        val response = restTemplate!!.exchange(
            URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + opprettResponse.body!!.oppgaveId,
            HttpMethod.GET,
            HttpEntity<Any?>(
                createHeaders(
                    Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"
                )
            ),
            HentDokumentResponse::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun journalpostManglerSkalKaste204() {
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            HttpEntity(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!)),
            OpprettServiceklageResponse::class.java
        )
        assertEquals(serviceklageRepository!!.count(), 1)
        val (_, _, _, _, _, _, _, _, _, _, _, fremmetDato1) = serviceklageRepository!!.findAll().iterator().next()
        val response = restTemplate!!.exchange(
            "$URL_BEHANDLE_SERVICEKLAGE/$HENT_DOKUMENT/99",
            HttpMethod.GET,
            HttpEntity<Any?>(createHeaders(Constants.AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering")),
            HentDokumentResponse::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun shouldReturn404WhenEregIsNotFound() {
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
    fun shouldReturn403WhenMissingAccessToJoarkDocument() {
        // Given
        val msg = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        val opprettResponse = restTemplate!!.exchange(
            URL_SENDINN_SERVICEKLAGE,
            HttpMethod.POST,
            HttpEntity<Any>(msg, createHeaders(Constants.AZURE_ISSUER, msg.innmelder!!.personnummer!!)),
            OpprettServiceklageResponse::class.java
        )
        WireMock.setScenarioState("hent_dokument", "saf_403")

        // When
        val response: ResponseEntity<ErrorResponse>? = restTemplate!!.exchange(
            URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + opprettResponse.body!!.oppgaveId,
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

}
