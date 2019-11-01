package no.nav.tilbakemeldingsmottak;

import static no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType.NAV_KONTAKTSENTER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.Tidsrom;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.Feiltype;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Innmelder;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvBedrift;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvPerson;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class TestUtils {

    public static final String NAVN_INNMELDER = "Innmelder Innmeldersen";
    public static final String PERSONNUMMER = "01010096460";
    public static final String TELEFONNUMMER = "81549300";
    public static final String EPOST = "innmelder@hotmail.com";
    public static final Boolean HAR_FULLMAKT = Boolean.TRUE;
    public static final String ROLLE = "Advokat";

    public static final String NAVN_PERSON = "Paal Vegne Personsen";

    public static final String NAVN_BEDRIFT= "Bedrift AS";
    public static final String ORGANISASJONSNUMMER= "123456789";
    public static final String POSTADRESSE= "Nedre Bedriftsgate 15A, 0168 OSLO";
    public static final String TELEFONNUMMER_BEDRIFT= "12345678";

    public static final Klagetype KLAGETYPE = Klagetype.NAVNO;
    public static final String KLAGETEKST = "Saksbehandleren var slem";
    public static final Boolean OENSKER_AA_KONTAKTES = Boolean.FALSE;

    public static final Feiltype FEILTYPE = Feiltype.TEKNISK_FEIL;
    public static final String BESKRIVELSE_FEIL = "Det er en teknisk feil på nav.no";

    public static final HvemRosesType HVEM_ROSES = NAV_KONTAKTSENTER;
    public static final String BESKRIVELSE_ROS = "Saksbehandleren var snill";
    public static final HvemRosesType HVEM_ROSES_KONTOR = HvemRosesType.NAV_KONTOR;
    public static final String NAV_KONTOR = "NAV Evje og Hornnes - 0937";

    public static final String FORNAVN = "Fred";
    public static final String ETTERNAVN = "Buljo";

    public static final String BEHANDLES_SOM_SERVICEKLAGE = "Ja";
    public static final String FREMMET_DATO = LocalDateTime.now().toString();
    public static final String INNSENDER = "Bruker selv som privatperson";
    public static final String KANAL = "Serviceklageskjema på nav.no";
    public static final String PAAKLAGET_ENHET_ER_BEHANDLENDE = "Nei";
    public static final String ENHETSNUMMER_PAAKLAGET = "1234";
    public static final String ENHETSNUMMER_BEHANDLENDE = "4321";
    public static final String GJELDER = "Gjelder én ytelse eller tjeneste";
    public static final String YTELSE = "AAP - Arbeidsavklaringspenger";
    public static final String TEMA = "Vente på NAV";
    public static final String VENTE = "Saksbehandlingstid";
    public static final String UTFALL = "a) Regler/rutiner/frister er fulgt - NAV har ivaretatt bruker godt";
    public static final String SVARMETODE = "Svar ikke nødvendig";
    public static final String SVAR_IKKE_NOEDVENDIG = "Bruker ikke bedt om svar";

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
                .epost(EPOST)
                .feiltype(FEILTYPE)
                .melding(BESKRIVELSE_FEIL)
                .build();
    }

    public static BestillSamtaleRequest createBestillSamtaleRequest() {
        return BestillSamtaleRequest.builder()
                .fornavn(FORNAVN)
                .etternavn(ETTERNAVN)
                .telefonnummer(TELEFONNUMMER)
                .tidsrom(Tidsrom.FORMIDDAG)
                .build();
    }

    public static SendRosRequest createSendRosRequest() {
        return SendRosRequest.builder()
                .navn(NAVN_INNMELDER)
                .hvemRoses(HVEM_ROSES)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static SendRosRequest createSendRosRequestWithNavKontor() {
        return SendRosRequest.builder()
                .navn(NAVN_INNMELDER)
                .hvemRoses(HVEM_ROSES_KONTOR)
                .navKontor(NAV_KONTOR)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static KlassifiserServiceklageRequest createKlassifiserServiceklageRequest() {
        return KlassifiserServiceklageRequest.builder()
                .answers(KlassifiserServiceklageRequest.Answers.builder()
                        .behandlesSomServiceklage(BEHANDLES_SOM_SERVICEKLAGE)
                        .fremmetDato(FREMMET_DATO)
                        .innsender(INNSENDER)
                        .kanal(KANAL)
                        .paaklagetEnhetErBehandlende(PAAKLAGET_ENHET_ER_BEHANDLENDE)
                        .enhetsnummerPaaklaget(ENHETSNUMMER_PAAKLAGET)
                        .enhetsnummerBehandlende(ENHETSNUMMER_BEHANDLENDE)
                        .gjelder(GJELDER)
                        .ytelse(YTELSE)
                        .tema(TEMA)
                        .vente(VENTE)
                        .utfall(UTFALL)
                        .svarmetode(SVARMETODE)
                        .svarIkkeNoedvendig(SVAR_IKKE_NOEDVENDIG)
                        .build()
                    )
                .build();
    }

    @SneakyThrows
    public static HentSkjemaResponse createHentSkjemaResponse() {
        InputStream schema = TestUtils.class.getClassLoader().getResourceAsStream("schema/schema.yaml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String classpathSkjema = StreamUtils.copyToString(schema, Charset.forName("utf-8"));
        return mapper.readValue(classpathSkjema, HentSkjemaResponse.class);
    }

}
