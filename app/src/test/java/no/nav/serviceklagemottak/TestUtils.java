package no.nav.serviceklagemottak;

import no.nav.serviceklagemottak.api.Innmelder;
import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.api.PaaVegneAvBedrift;
import no.nav.serviceklagemottak.api.PaaVegneAvPerson;
import no.nav.serviceklagemottak.api.PaaVegneAvType;

public class TestUtils {

    private static final String NAVN_INNMELDER = "Innmelder Innmeldersen";
    private static final String PERSONNUMMER_INNMELDER = "0101200012345";
    private static final String TELEFONNUMMER_INNMELDER = "81549300";
    private static final Boolean HAR_FULLMAKT = Boolean.TRUE;
    private static final String ROLLE = "Advokat";

    private static final String NAVN_PERSON = "Paal Vegne Personsen";
    private static final String PERSONNUMMER_PERSON = "0203200112345";

    private static final String NAVN_BEDRIFT= "Bedrift AS";
    private static final String ORGANISASJONSNUMMER= "123456789";
    private static final String POSTADRESSE= "Nedre Bedriftsgate 15A, 0168 OSLO";
    private static final String TELEFONNUMMER_BEDRIFT= "12345678";

    private static final String KLAGETYPE = "Nav.no";
    private static final String KLAGETEKST = "Saksbehandleren var slem";
    private static final Boolean OENSKER_AA_KONTAKTES = Boolean.TRUE;

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPrivatperson() {
        return OpprettServiceklageRequest.builder()
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .personnummer(PERSONNUMMER_INNMELDER)
                        .telefonnummer(TELEFONNUMMER_INNMELDER)
                        .build())
                .klagetype(KLAGETYPE)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvPerson() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvType.PERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .personnummer(PERSONNUMMER_INNMELDER)
                        .harFullmakt(HAR_FULLMAKT)
                        .rolle(ROLLE)
                        .build())
                .paaVegneAvPerson(PaaVegneAvPerson.builder()
                        .navn(NAVN_PERSON)
                        .personnummer(PERSONNUMMER_PERSON)
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
                        .personnummer(PERSONNUMMER_INNMELDER)
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
}
