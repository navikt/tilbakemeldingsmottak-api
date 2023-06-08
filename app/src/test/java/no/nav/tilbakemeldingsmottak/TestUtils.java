package no.nav.tilbakemeldingsmottak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import no.nav.tilbakemeldingsmottak.consumer.norg2.Enhet;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.DataJournalpost;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.Variantformat;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.graphql.IdentGruppe;
import no.nav.tilbakemeldingsmottak.graphql.IdentInformasjon;
import no.nav.tilbakemeldingsmottak.graphql.Identliste;
import no.nav.tilbakemeldingsmottak.model.*;
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest.TidsromEnum;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest.FeiltypeEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.GjelderSosialhjelpEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAvEnum;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest.HvemRosesEnum;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.*;
import static no.nav.tilbakemeldingsmottak.util.SkjemaUtils.getQuestionById;

public class TestUtils {

    public static final String NAVN_INNMELDER = "Innmelder Innmeldersen";
    public static final String PERSONNUMMER = "01010096460";
    public static final String AKTOERID = "1234567890123";
    public static final String TELEFONNUMMER = "81549300";
    public static final String EPOST = "innmelder@hotmail.com";
    public static final Boolean HAR_FULLMAKT = Boolean.TRUE;
    public static final String ROLLE = "Advokat";

    public static final String NAVN_PERSON = "Paal Vegne Personsen";

    public static final String NAVN_BEDRIFT = "Bedrift AS";
    public static final String ORGANISASJONSNUMMER = "123456789";

    public static final List<KlagetyperEnum> KLAGETYPER = Collections.singletonList(KlagetyperEnum.NAV_DIGITALE_TJENESTER);
    public static final List<KlagetyperEnum> KLAGETYPER_NAV_KONTOR = Collections.singletonList(KlagetyperEnum.LOKALT_NAV_KONTOR);
    public static final String KLAGETEKST = "Saksbehandleren var slem";
    public static final Boolean OENSKER_AA_KONTAKTES = Boolean.FALSE;

    public static final FeiltypeEnum FEILTYPE = FeiltypeEnum.TEKNISK_FEIL;
    public static final String BESKRIVELSE_FEIL = "Det er en teknisk feil på nav.no";
    public static final HvemRosesEnum HVEM_ROSES = HvemRosesEnum.NAV_KONTAKTSENTER;
    public static final String BESKRIVELSE_ROS = "Saksbehandleren var snill";
    public static final HvemRosesEnum HVEM_ROSES_KONTOR = HvemRosesEnum.NAV_KONTOR;
    public static final String NAV_ENHETSNAVN_1 = "NAV Evje og Hornnes";
    public static final String NAV_ENHETSNAVN_2 = "NAV Aremark";
    public static final String NAV_ENHETSNAVN_3 = "NAV Bamble";
    public static final String NAV_ENHETSNAVN_4 = "NAV Nordkapp";
    public static final String NAV_ENHETSNAVN_5 = "NAV Smøla";
    public static final String NAV_ENHETSNR_1 = "0937";
    public static final String NAV_ENHETSNR_2 = "0118";
    public static final String NAV_ENHETSNR_3 = "0814";
    public static final String NAV_ENHETSNR_4 = "2019";
    public static final String NAV_ENHETSNR_5 = "1573";
    public static final String NAV_KONTOR_1 = NAV_ENHETSNAVN_1 + " - " + NAV_ENHETSNR_1;
    public static final String NAV_KONTOR_2 = NAV_ENHETSNAVN_2 + " - " + NAV_ENHETSNR_2;
    public static final String NAV_KONTOR_3 = NAV_ENHETSNAVN_3 + " - " + NAV_ENHETSNR_3;
    public static final String NAV_KONTOR_4 = NAV_ENHETSNAVN_4 + " - " + NAV_ENHETSNR_4;
    public static final String NAV_KONTOR_5 = NAV_ENHETSNAVN_5 + " - " + NAV_ENHETSNR_5;
    public static final String NAV_ENHET_STATUS = "Aktiv";
    public static final String FORNAVN = "Fred";
    public static final String ETTERNAVN = "Buljo";
    public static final String BEHANDLES_SOM_SERVICEKLAGE = "Ja";
    public static final String FREMMET_DATO = LocalDate.now().toString();
    public static final String INNSENDER = "Bruker selv som privatperson";
    public static final String PAAKLAGET_ENHET_ER_BEHANDLENDE = "Nei";
    public static final String GJELDER = "Gjelder én ytelse eller tjeneste";
    public static final String BESKRIVELSE = "Bruker klager på service";
    public static final String YTELSE = "AAP - Arbeidsavklaringspenger";
    public static final String RELATERT = "EØS-saken,Åpningstider på NAV-kontoret";
    public static final String TEMA = "Vente på NAV";
    public static final String VENTE = "Saksbehandlingstid";
    public static final String UTFALL = "b) Regler/rutiner/frister er fulgt men NAV burde ivaretatt bruker bedre";
    public static final String AARSAK = "Service har vært dårlig";
    public static final String TILTAK = "Gi bedre service";
    public static final String KVITTERING = "Nei";
    public static final String BEHANDLES_IKKE_SOM_SERVICEKLAGE = "Nei, annet";
    public static final String KOMMUNAL_KLAGE = "Nei, serviceklagen gjelder kommunale tjenester eller ytelser";
    public static final String FORVALTNINGSKLAGE = "Nei, en forvaltningsklage";
    public static final String FULGT_BRUKERVEILEDNING_GOSYS = "Ja";
    public static final String KOMMUNAL_BEHANDLING = "Ja";
    public static final String SAKSBEHANDLER = "saksbehandler";
    public static final String DOKUMENT_INFO_ID = "dokumentInfoId";
    private static final Boolean ONSKER_KONTAKT = Boolean.TRUE;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPrivatperson() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.PRIVATPERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .personnummer(PERSONNUMMER)
                        .build())
                .klagetyper(KLAGETYPER)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvPerson() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.ANNEN_PERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .harFullmakt(HAR_FULLMAKT)
                        .personnummer(PERSONNUMMER)
                        .rolle(ROLLE)
                        .build())
                .paaVegneAvPerson(PaaVegneAvPerson.builder()
                        .navn(NAVN_PERSON)
                        .personnummer(PERSONNUMMER)
                        .build())
                .klagetyper(KLAGETYPER)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPaaVegneAvBedrift() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.BEDRIFT)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .rolle(ROLLE)
                        .build())
                .paaVegneAvBedrift(PaaVegneAvBedrift.builder()
                        .navn(NAVN_BEDRIFT)
                        .organisasjonsnummer(ORGANISASJONSNUMMER)
                        .build())
                .enhetsnummerPaaklaget(NAV_ENHETSNR_1)
                .klagetyper(KLAGETYPER)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static OpprettServiceklageRequest createOpprettServiceklageRequestPrivatpersonLokaltKontor() {
        return OpprettServiceklageRequest.builder()
                .paaVegneAv(PaaVegneAvEnum.PRIVATPERSON)
                .innmelder(Innmelder.builder()
                        .navn(NAVN_INNMELDER)
                        .telefonnummer(TELEFONNUMMER)
                        .personnummer(PERSONNUMMER)
                        .build())
                .klagetyper(KLAGETYPER_NAV_KONTOR)
                .gjelderSosialhjelp(GjelderSosialhjelpEnum.JA)
                .klagetekst(KLAGETEKST)
                .oenskerAaKontaktes(OENSKER_AA_KONTAKTES)
                .build();
    }

    public static MeldFeilOgManglerRequest createMeldFeilOgManglerRequest() {
        return MeldFeilOgManglerRequest.builder()
                .onskerKontakt(ONSKER_KONTAKT)
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
                .tidsrom(TidsromEnum.FORMIDDAG)
                .build();
    }

    public static SendRosRequest createSendRosRequest() {
        return SendRosRequest.builder()
                .hvemRoses(HVEM_ROSES)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static SendRosRequest createSendRosRequestWithNavKontor() {
        return SendRosRequest.builder()
                .hvemRoses(HVEM_ROSES_KONTOR)
                .navKontor(NAV_KONTOR_1)
                .melding(BESKRIVELSE_ROS)
                .build();
    }

    public static KlassifiserServiceklageRequest createKlassifiserServiceklageRequest() {
        return KlassifiserServiceklageRequest.builder()
                .BEHANDLES_SOM_SERVICEKLAGE(BEHANDLES_SOM_SERVICEKLAGE)
                .FREMMET_DATO(FREMMET_DATO)
                .INNSENDER(INNSENDER)
                .KANAL(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .PAAKLAGET_ENHET_ER_BEHANDLENDE(PAAKLAGET_ENHET_ER_BEHANDLENDE)
                .ENHETSNUMMER_PAAKLAGET(NAV_KONTOR_1)
                .ENHETSNUMMER_BEHANDLENDE(NAV_KONTOR_2)
                .GJELDER(GJELDER)
                .BESKRIVELSE(BESKRIVELSE)
                .YTELSE(YTELSE)
                .RELATERT(RELATERT)
                .TEMA(TEMA)
                .VENTE(VENTE)
                .UTFALL(UTFALL)
                .AARSAK(AARSAK)
                .TILTAK(TILTAK)
                .SVARMETODE(SVAR_IKKE_NOEDVENDIG_ANSWER)
                .SVAR_IKKE_NOEDVENDIG(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .KVITTERING(KVITTERING)
                .build();
    }

    public static KlassifiserServiceklageRequest createKlassifiserServiceklageRequestIkkeServiceklage() {
        return KlassifiserServiceklageRequest.builder()
                .BEHANDLES_SOM_SERVICEKLAGE(BEHANDLES_IKKE_SOM_SERVICEKLAGE)
                .FULGT_BRUKERVEILEDNING_GOSYS(FULGT_BRUKERVEILEDNING_GOSYS)
                .INNSENDER(INNSENDER)
                .KANAL(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .SVARMETODE(SVAR_IKKE_NOEDVENDIG_ANSWER)
                .SVAR_IKKE_NOEDVENDIG(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .KVITTERING(KVITTERING)
                .build();
    }

    public static KlassifiserServiceklageRequest createKlassifiserServiceklageRequestKommunalKlage() {
        return KlassifiserServiceklageRequest.builder()
                .BEHANDLES_SOM_SERVICEKLAGE(KOMMUNAL_KLAGE)
                .KOMMUNAL_BEHANDLING(KOMMUNAL_BEHANDLING)
                .INNSENDER(INNSENDER)
                .KANAL(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .SVARMETODE(SVAR_IKKE_NOEDVENDIG_ANSWER)
                .SVAR_IKKE_NOEDVENDIG(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .KVITTERING(KVITTERING)
                .build();
    }

    public static KlassifiserServiceklageRequest createKlassifiserServiceklageRequestForvaltningsklage() {
        return KlassifiserServiceklageRequest.builder()
                .BEHANDLES_SOM_SERVICEKLAGE(FORVALTNINGSKLAGE)
                .FULGT_BRUKERVEILEDNING_GOSYS(FULGT_BRUKERVEILEDNING_GOSYS)
                .INNSENDER(INNSENDER)
                .KANAL(KANAL_SERVICEKLAGESKJEMA_ANSWER)
                .SVARMETODE(SVAR_IKKE_NOEDVENDIG_ANSWER)
                .SVAR_IKKE_NOEDVENDIG(BRUKER_IKKE_BEDT_OM_SVAR_ANSWER)
                .KVITTERING(KVITTERING)
                .build();
    }

    @SneakyThrows
    public static HentSkjemaResponse createHentSkjemaResponse() {
        InputStream schema = TestUtils.class.getClassLoader().getResourceAsStream("schema/schema.yaml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String classpathSkjema = StreamUtils.copyToString(schema, StandardCharsets.UTF_8);
        HentSkjemaResponse response = mapper.readValue(classpathSkjema, HentSkjemaResponse.class);

        List<Answer> answers = Stream.of(NAV_KONTOR_1, NAV_KONTOR_2, NAV_KONTOR_3, NAV_KONTOR_4, NAV_KONTOR_5)
                .map(e -> Answer.builder()
                        .answer(e)
                        .build())
                .collect(Collectors.toList());

        getQuestionById(response.getQuestions(), ENHETSNUMMER_PAAKLAGET)
                .orElseThrow(() -> new ServerErrorException("Finner ikke spørsmål med id=" + ENHETSNUMMER_PAAKLAGET))
                .setAnswers(answers);

        getQuestionById(response.getQuestions(), ENHETSNUMMER_BEHANDLENDE)
                .orElseThrow(() -> new ServerErrorException("Finner ikke spørsmål med id=" + ENHETSNUMMER_BEHANDLENDE))
                .setAnswers(answers);

        return response;
    }

    public static HentSkjemaResponse createHentSkjemaResponseWithDefaultAnswers() {
        HentSkjemaResponse response = createHentSkjemaResponse();
        Map<String, String> answers = new HashMap<>();
        answers.put(KANAL, KANAL_SERVICEKLAGESKJEMA_ANSWER);
        response.setDefaultAnswers(DefaultAnswers.builder()
                .answers(answers)
                .build());
        return response;
    }

    public static Identliste createHentAktoerIdForIdentResponse(String aktoerId) {
        return Identliste.builder()
                .withIdenter(List.of(IdentInformasjon.builder()
                        .withIdent(aktoerId)
                        .withGruppe(IdentGruppe.AKTORID)
                        .withHistorisk(false)
                        .build())).build();
    }

    public static Identliste createEmptyHentAktoerIdForIdentResponse() {
        return Identliste.builder().withIdenter(Collections.emptyList()).build();
    }

    @SneakyThrows
    public static String createNorg2Response() {
        List<Enhet> enheter = Arrays.asList(
                Enhet.builder().enhetNr(NAV_ENHETSNR_1).navn(NAV_ENHETSNAVN_1).status(NAV_ENHET_STATUS).build(),
                Enhet.builder().enhetNr(NAV_ENHETSNR_2).navn(NAV_ENHETSNAVN_2).status(NAV_ENHET_STATUS).build(),
                Enhet.builder().enhetNr(NAV_ENHETSNR_3).navn(NAV_ENHETSNAVN_3).status(NAV_ENHET_STATUS).build(),
                Enhet.builder().enhetNr(NAV_ENHETSNR_4).navn(NAV_ENHETSNAVN_4).status(NAV_ENHET_STATUS).build(),
                Enhet.builder().enhetNr(NAV_ENHETSNR_5).navn(NAV_ENHETSNAVN_5).status(NAV_ENHET_STATUS).build()
        );

        return objectMapper.writeValueAsString(enheter);
    }

    @SneakyThrows
    public static String createSafGraphqlResponse() {
        SafJournalpostTo safJournalpostTo = SafJournalpostTo.builder()
                .dokumenter(Collections.singletonList(SafJournalpostTo.DokumentInfo.builder()
                        .dokumentInfoId(DOKUMENT_INFO_ID)
                        .dokumentvarianter(Arrays.asList(
                                SafJournalpostTo.Dokumentvariant.builder()
                                        .variantformat(Variantformat.ARKIV.name())
                                        .saksbehandlerHarTilgang(true)
                                        .build(),
                                SafJournalpostTo.Dokumentvariant.builder()
                                        .variantformat(Variantformat.SLADDET.name())
                                        .saksbehandlerHarTilgang(true)
                                        .build()))
                        .build()))
                .build();

        DataJournalpost dataJournalpost = new DataJournalpost();
        dataJournalpost.setJournalpost(safJournalpostTo);

        SafJsonJournalpost safJsonJournalpost = new SafJsonJournalpost();
        safJsonJournalpost.setData(dataJournalpost);

        return objectMapper.writeValueAsString(safJsonJournalpost);
    }

    @SneakyThrows
    public static String createSafGraphqlNoDocumentsResponse() {
        SafJournalpostTo safJournalpostTo = SafJournalpostTo.builder()
                .dokumenter(Collections.singletonList(SafJournalpostTo.DokumentInfo.builder()
                        .dokumentInfoId(DOKUMENT_INFO_ID)
                        .dokumentvarianter(List.of())
                        .build()))
                .build();

        DataJournalpost dataJournalpost = new DataJournalpost();
        dataJournalpost.setJournalpost(safJournalpostTo);

        SafJsonJournalpost safJsonJournalpost = new SafJsonJournalpost();
        safJsonJournalpost.setData(dataJournalpost);

        return objectMapper.writeValueAsString(safJsonJournalpost);
    }

    public static String getStringFromByteArrayPdf(byte[] bytes) throws IOException {
        InputStream documentStream = new ByteArrayInputStream(bytes);
        PDDocument document = PDDocument.load(documentStream);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
}
