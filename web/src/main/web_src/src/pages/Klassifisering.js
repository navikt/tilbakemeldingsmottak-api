import React, { Component } from "react";
import "./Klassifisering.less";
import * as queryString from "query-string";
import SkjemaRender from "../components/SkjemaRender";
import SkjemaMapper from "../components/SkjemaMapper";
import Dokument from "../components/Dokument";

const schema = {
  questions: [
    {
      title: "1) Skal den behandles etter serviceklagerutinen",
      type: "radio",
      answers: [
        "Ja",
        { answer: "Ja, men inneholder også andre elementer", next: "none" },
        {
          answer: "Nei, innsender har ikke fullmakt på vegne av bruker",
          next: "none"
        },
        {
          answer:
            "Nei, serviceklagen gjelder kommunale tjenester eller ytelser",
          next: "none"
        },
        { answer: "Nei - en forvaltningsklage", next: "none" },
        { answer: "Nei – en beskjed til NAV", next: "none" }
      ]
    },
    {
      title: "2) Angi dato bruker fremmet serviceklagen",
      type: "input"
    },
    {
      title: "3) Hvem sendte inn serviceklagen",
      type: "radio",
      answers: [
        "Bruker selv som privatperson",
        "På vegne av en annen privatperson",
        "På vegne av virksomhet"
      ]
    },
    {
      title: "4) Kanal for serviceklagen",
      type: "radio",
      answers: [
        "Serviceklageskjema på nav.no",
        "Digital kanal på NAV.no",
        "Annen skriftlig kanal fra bruker",
        "Muntlig fra bruker",
        "Annet"
      ]
    },
    {
      title: "5) Er klagen behandlet i enheten det klages på?",
      type: "radio",
      answers: ["Ja", "Nei"]
    },
    {
      title: "6) Enhetsnummer til enheten det klages på",
      type: "input"
    },
    {
      title: "7) Angi enhetsnummer til enhet som behandler klagen",
      type: "input"
    },
    {
      title: "8a) Hva mener du serviceklagen gjelder?",
      type: "radio",
      answers: [
        "Gjelder én ytelse eller tjeneste",
        "Gjelder flere ting eller sammensatt situasjon"
      ]
    },
    {
      title: "8b) Hvilken ytelse mm. er mest relevant for serviceklagen",
      type: "select",
      answers: [
        "AFP - Avtalefestet pensjon",
        "Alderspensjon",
        "AAP - Arbeidsavklaringspenger",
        "Arbeidsmarkedsloven",
        "Barnebidragsområdet",
        "Barnepensjon",
        "Barnetrygd",
        "Barns sykdom",
        "Bil",
        "Dagpenger",
        "Eneforsørger",
        "Foreldrepenger",
        "Gjenlevende ektefelle",
        "Grunnstønad/hjelpestønad",
        "Hjelpemidler",
        "Kontantstøtte",
        "Medlemskap",
        "Supplerende stønad",
        "Sykepenger",
        "Uførepensjon",
        "Yrkesskade/yrkessykdom",
        "Annet",
        "Uspesifisert"
      ]
    },
    {
      title: "9) Hva gjelder serviceklagen?",
      type: "radio",
      answers: [
        {
          answer: "Vente på NAV",
          questions: [
            {
              title: "Vente på Nav",
              type: "radio",
              answers: [
                "Saksbehandlingstid",
                "Svartid innsyn",
                "Svartid telefon",
                "Ventetid kontakt bruker",
                "Svartid/ventetid i andre tilfeller"
              ]
            }
          ]
        },
        {
          answer: "Tilgjengelighet",
          questions: [
            {
              title: "Tilgjengelighet",
              type: "radio",
              answers: [
                "Mulighet til kontakt i dagens kanaler",
                "Nedetid i NAVs løsninger",
                "Kanal mangler"
              ]
            }
          ]
        },
        {
          answer: "Informasjon",
          questions: [
            {
              title: "Informasjon",
              type: "radio",
              answers: [
                "Muntlig over telefonen",
                "Muntlig ved oppmøte/møte",
                "På nav.no /andre brukerflater i NAV",
                "I brev"
              ]
            }
          ]
        },
        "Funksjonalitet i NAVs løsninger",
        {
          answer: "Veiledning og oppfølging mot arbeid",
          questions: [
            {
              title: "Veiledning og oppfølging mot arbeid",
              type: "radio",
              answers: [
                "Muntlig over telefon",
                "Muntlig ved oppmøte/møte",
                "På nav.no/andre brukerflater i NAV"
              ]
            }
          ]
        },
        "NAV-ansattes oppførsel",
        "Annet"
      ]
    },
    {
      title: "10) Utfallet av serviceklagen",
      type: "radio",
      answers: [
        "a) Regler/rutiner/frister er fulgt – NAV har ivaretatt bruker godt",
        {
          answer:
            "b) Regler/rutiner/frister er fulgt men NAV burde ivaretatt bruker bedre",
        },
        {
          answer:
            "c) Regler/rutiner/frister er brutt – NAV burde ivaretatt bruker bedre",
          questions: [
            {
              title: "Hva er, etter din mening, de bakenforliggende årsakene?",
              type: "input"
            },
            {
              title:
                "Hvordan kan NAV hindre at det samme skjer igjen, etter din mening?",
              type: "input"
            }
          ]
        },
        "d) Prøvd, men ikke klart å finne ut hva som har skjedd"
      ]
    },
    {
      title: "12) Hvordan har bruker blitt svart",
      type: "radio",
      answers: [
        "Møte",
        "Telefon",
        "Modia",
        "Manuell post (brev)",
        "E-post",
        {
          answer: "Svar ikke nødvendig",
          questions: [
            {
              title: "Svar ikke nødvendig",
              type: "radio",
              answers: [
                "Bruker ikke bedt om svar",
                {
                  answer: "Annet",
                  questions: [
                    {
                      title: "Spesifiser",
                      type: "input"
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
};

class Klassifisering extends Component {
  constructor(props) {
    super(props);
    let params = queryString.parse(this.props.location.search);
    this.journalpostId = params.journalpostId;
    this.oppgaveId = params.oppgaveId;
  }

  render() {
    return (
      <div className="Row">
        <div className="Klassifisering">
          <SkjemaRender
            schema={SkjemaMapper(schema)}
            journalpostId={this.journalpostId}
            oppgaveId={this.oppgaveId}
          />
        </div>

        <div>
          <div className="Dokument">
            <Dokument />
          </div>
        </div>
      </div>
    );
  }
}

export default Klassifisering;
