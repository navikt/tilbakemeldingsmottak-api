package no.nav.tilbakemeldingsmottak.validators

import no.nav.tilbakemeldingsmottak.TestUtils
import no.nav.tilbakemeldingsmottak.consumer.ereg.EregConsumer
import no.nav.tilbakemeldingsmottak.consumer.pdl.PdlService
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageKlagetype.LOKALT_NAV_KONTOR
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.rest.common.validation.PersonnummerValidator
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import no.nav.tilbakemeldingsmottak.util.builders.InnmelderBuilder
import no.nav.tilbakemeldingsmottak.util.builders.OpprettServiceklageRequestBuilder
import no.nav.tilbakemeldingsmottak.util.builders.PaaVegneAvBedriftBuilder
import no.nav.tilbakemeldingsmottak.util.builders.PaaVegneAvPersonBuilder
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class OpprettServiceklageValidatorTest {
    @Mock
    var eregConsumer: EregConsumer? = null

    @Mock
    var pdlService: PdlService? = null

    @Mock
    var oidcUtils: OidcUtils? = null

    @Mock
    var personnummerValidator: PersonnummerValidator? = null

    @InjectMocks
    var opprettServiceklageValidator: OpprettServiceklageValidator? = null

    private var opprettServiceklageRequest: OpprettServiceklageRequest = OpprettServiceklageRequestBuilder().build()

    @BeforeEach
    fun setup() {
        Mockito.lenient().`when`(eregConsumer!!.hentInfo(ArgumentMatchers.anyString())).thenReturn("")
        Mockito.lenient().`when`(pdlService!!.hentAktorIdForIdent(ArgumentMatchers.anyString()))
            .thenReturn(TestUtils.AKTOERID)
        Mockito.lenient().`when`(oidcUtils!!.getSubjectForIssuer(ArgumentMatchers.anyString()))
            .thenReturn("")
    }

    @Test
    fun happyPathPrivatperson() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
    }

    @Test
    fun happyPathPaaVegneAvPerson() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build()
        opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
    }

    @Test
    fun happyPathPaaVegneAvBedrift() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build()
        opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
    }

    @Test
    fun happyPathInnlogget() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()
        opprettServiceklageValidator!!.validateRequest(
            opprettServiceklageRequest,
            opprettServiceklageRequest.innmelder!!.personnummer
        )
    }

    @Test
    fun shouldThrowExceptionIfKlagetypeNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().build(klagetyper = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("klagetyper er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfGjelderSosialhjelpNotSetForLokaltkontor() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build(
            klagetyper = listOf(LOKALT_NAV_KONTOR),
            gjelderSosialhjelp = null
        )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("gjelderSosialhjelp er påkrevd dersom klagetyper=LOKALT_NAV_KONTOR"))
    }

    @Test
    fun shouldThrowExceptionIfKlagetekstNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build(klagetekst = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("klagetekst er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfOenskerAaKontaktesNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson().build(oenskerAaKontaktes = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("oenskerAaKontaktes er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build(paaVegneAv = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAv er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfInnmelderNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build(innmelder = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfInnmelderNavnNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson()
                .build(innmelder = InnmelderBuilder().build(navn = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.navn er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfInnmelderTelefonnummerNotSetAndOenskerAaKontaktesIsTrue() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build(
            oenskerAaKontaktes = true,
            innmelder = InnmelderBuilder().build(telefonnummer = null)
        )
        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"))
    }

    @Test
    fun shouldThrowExceptionIfPersonnummerNotSetForPrivatperson() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPerson()
                .build(innmelder = InnmelderBuilder().build(personnummer = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.personnummer er påkrevd dersom paaVegneAv=PRIVATPERSON"))
    }

    @Test
    fun shouldThrowExceptionIfHarFullmaktNotSetForPaaVegneAvPerson() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
                .build(innmelder = InnmelderBuilder().build(rolle = "Advokat", harFullmakt = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.harFullmakt er påkrevd dersom paaVegneAv=ANNEN_PERSON"))
    }

    @Test
    fun shouldThrowExceptionIfRolleNotSetforPaaVegneAvPerson() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
                .build(innmelder = InnmelderBuilder().build(rolle = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.rolle er påkrevd dersom paaVegneAv=ANNEN_PERSON"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvPersonNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build(paaVegneAvPerson = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvPerson er påkrevd dersom paaVegneAv=ANNEN_PERSON"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvPersonNavnNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
                .build(paaVegneAvPerson = PaaVegneAvPersonBuilder().build(navn = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvPerson.navn er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvPersonPersonnummerNotSet() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv()
                .build(paaVegneAvPerson = PaaVegneAvPersonBuilder().build(personnummer = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvPerson.personnummer er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvPersonAndOenskerAaKontaktesIsSetWithoutFullmakt() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build(
            oenskerAaKontaktes = true,
            innmelder = InnmelderBuilder().build(rolle = "Advokat", telefonnummer = null, harFullmakt = false)
        )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("oenskerAaKontaktes kan ikke være satt dersom klagen er meldt inn på vegne av annen person uten fullmakt"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvPersonAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPersonPaaVegneAv().build(
            oenskerAaKontaktes = true,
            innmelder = InnmelderBuilder().build(rolle = "Advokat", telefonnummer = null, harFullmakt = true)
        )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittTelefonnummer() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift()
            .build(
                oenskerAaKontaktes = true,
                innmelder = InnmelderBuilder().build(rolle = "CEO", telefonnummer = null, harFullmakt = true)
            )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.telefonnummer er påkrevd dersom oenskerAaKontaktes=true"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftAndInnmelderOenskerAaKontakesUtenOppgittNavn() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build(
            oenskerAaKontaktes = true,
            innmelder = InnmelderBuilder().build(navn = null)
        )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("innmelder.navn er påkrevd dersom paaVegneAv=BEDRIFT og oenskerAaKontaktes=true"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build(paaVegneAvBedrift = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvBedrift er påkrevd dersom paaVegneAv=BEDRIFT"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftNavnNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build(
            paaVegneAvBedrift =
            PaaVegneAvBedriftBuilder().build(navn = null)
        )

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvBedrift.navn er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftOrganisasjonsnummerNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift()
            .build(paaVegneAvBedrift = PaaVegneAvBedriftBuilder().build(organisasjonsnummer = null))

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("paaVegneAvBedrift.organisasjonsnummer er påkrevd"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerNotSet() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asBedrift().build(enhetsnummerPaaklaget = null)

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("enhetsnummerPaaklaget er påkrevd dersom paaVegneAv=BEDRIFT"))
    }

    @Test
    fun shouldThrowExceptionIfPaaVegneAvBedriftEnhetsnummerWrongFormat() {
        opprettServiceklageRequest =
            OpprettServiceklageRequestBuilder().asBedrift().build(enhetsnummerPaaklaget = "123abc")

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(opprettServiceklageRequest, null)
        }

        assertTrue(thrown.message.contains("enhetsnummerPaaklaget må ha fire siffer"))
    }

    @Test
    fun shouldThrowExceptionIfPersonnummerDoesntMatchTokenIdent() {
        opprettServiceklageRequest = OpprettServiceklageRequestBuilder().asPrivatPerson().build()

        val thrown = assertThrows(ClientErrorException::class.java) {
            opprettServiceklageValidator!!.validateRequest(
                opprettServiceklageRequest,
                "12345678901"
            )
        }

        assertTrue(thrown.message.contains("innmelder.personnummer samsvarer ikke med brukertoken"))
    }
}