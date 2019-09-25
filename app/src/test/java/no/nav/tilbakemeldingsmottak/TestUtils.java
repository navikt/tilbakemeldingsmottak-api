package no.nav.tilbakemeldingsmottak;

import static no.nav.tilbakemeldingsmottak.api.HvemRosesType.NAV_KONTAKTSENTER;

import no.nav.tilbakemeldingsmottak.api.Feiltype;
import no.nav.tilbakemeldingsmottak.api.HvemRosesType;
import no.nav.tilbakemeldingsmottak.api.Innmelder;
import no.nav.tilbakemeldingsmottak.api.Klagetype;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvBedrift;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.api.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.api.SendRosRequest;

import java.util.Arrays;
import java.util.List;

public class TestUtils {

    public static final String NAVN_INNMELDER = "Innmelder Innmeldersen";
    public static final String PERSONNUMMER = "01010096460";
    public static final String TELEFONNUMMER = "81549300";
    public static final Boolean HAR_FULLMAKT = Boolean.TRUE;
    public static final String ROLLE = "Advokat";

    public static final String NAVN_PERSON = "Paal Vegne Personsen";

    public static final String NAVN_BEDRIFT= "Bedrift AS";
    public static final String ORGANISASJONSNUMMER= "123456789";
    public static final String POSTADRESSE= "Nedre Bedriftsgate 15A, 0168 OSLO";
    public static final String TELEFONNUMMER_BEDRIFT= "12345678";

    public static final Klagetype KLAGETYPE = Klagetype.NAVNO;
    public static final String KLAGETEKST = "Saksbehandleren var slem";
    public static final Boolean OENSKER_AA_KONTAKTES = Boolean.TRUE;

    public static final Feiltype FEILTYPE = Feiltype.TEKNISK_FEIL;
    public static final String BESKRIVELSE_FEIL = "Det er en teknisk feil på nav.no";

    public static final HvemRosesType HVEM_ROSES = NAV_KONTAKTSENTER;
    public static final String BESKRIVELSE_ROS = "Saksbehandleren var snill";
    public static final HvemRosesType HVEM_ROSES_KONTOR = HvemRosesType.NAV_KONTOR;
    public static final String NAV_KONTOR = "NAV Evje og Hornnes - 0937";


    public static final String ER_SERVICEKLAGE = "Ja (inkludert saker som også har andre elementer)";
    public static final String KANAL = "nav.no";
    public static final String PAAKLAGET_ENHET = "1234";
    public static final String BEHANDLENDE_ENHET = "4321";
    public static final String YTELSE_TJENESTE = "Alderspensjon";
    public static final String TEMA = "Saksbehandling og svartid";
    public static final String UTFALL = "Bruker har ikke fått svar innen frist";
    public static final List<String> SVARMETODE = Arrays.asList("Avtalt møte");

    public static final String NEI_ANNET = "Nei - annet";
    public static final String GJELDER = "Klagen gjelder noe annet";


    public static OpprettServiceklageRequest createOpprettServiceklageRequestPrivatperson() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvType.PRIVATPERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .personnummer(PERSONNUMMER)
                        .build())
                .klagetype(KLAGETYPE)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvPerson() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvType.ANNEN_PERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .harFullmakt(HAR_FULLMAKT)
                        .rolle(ROLLE)
                        .build())
                .paaVegneAvPerson(PaaVegneAvPerson.builder()
                        .navn(NAVN_PERSON)
                        .personnummer(PERSONNUMMER)
                        .build())
                .klagetype(KLAGETYPE)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvBedrift() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvType.BEDRIFT)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .rolle(ROLLE)
                        .build())
                .paaVegneAvBedrift(PaaVegneAvBedrift.builder()
                        .navn(NAVN_BEDRIFT)
                        .organisasjonsnummer(ORGANISASJONSNUMMER)
                        .postadresse(POSTADRESSE)
                        .telefonnummer(TELEFONNUMMER_BEDRIFT)
                        .build())
                .klagetype(KLAGETYPE)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static MeldFeilOgManglerRequest createMeldFeilOgManglerRequest() {
        return MeldFeilOgManglerRequest.builder()
                .navn(NAVN_INNMELDER)
                .telefonnummer(TELEFONNUMMER)
                .feiltype(FEILTYPE)
                .melding(BESKRIVELSE_FEIL)
                .build();
    }

    public static SendRosRequest createSendRosRequest() {
        return SendRosRequest.builder()
                .navn(NAVN_INNMELDER)
                .telefonnummer(TELEFONNUMMER)
                .hvemRoses(HVEM_ROSES)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static SendRosRequest createSendRosRequestWithNavKontor() {
        return SendRosRequest.builder()
                .navn(NAVN_INNMELDER)
                .telefonnummer(TELEFONNUMMER)
                .hvemRoses(HVEM_ROSES_KONTOR)
                .navKontor(NAV_KONTOR)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static RegistrerTilbakemeldingRequest createRegistrerTilbakemeldingRequest() {
        return RegistrerTilbakemeldingRequest.builder()
                .erServiceklage(ER_SERVICEKLAGE)
                .kanal(KANAL)
                .paaklagetEnhet(PAAKLAGET_ENHET)
                .behandlendeEnhet(BEHANDLENDE_ENHET)
                .ytelseTjeneste(YTELSE_TJENESTE)
                .tema(TEMA)
                .utfall(UTFALL)
                .svarmetode(SVARMETODE)
                .build();
    }

    public static RegistrerTilbakemeldingRequest createRegistrerTilbakemeldingRequestNotServiceklage() {
        return RegistrerTilbakemeldingRequest.builder()
                .erServiceklage(NEI_ANNET)
                .gjelder(GJELDER)
                .build();
    }

}
