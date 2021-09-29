import React, {Component} from "react";
import {connect} from "react-redux";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import AlertStripe from "nav-frontend-alertstriper";
import queryString from "query-string";
import SchemaRender from "../components/schema/SchemaRender";
import {ServiceklageApi} from "../api/Api";
import {DefaultAnswersMapper, SchemaMapper} from "../mappers/skjema/SkjemaMapper";
import Dokument from "../components/Dokument";
import {RESET_KLASSIFISERING, UPDATE_SCHEMA} from "../store/actions";
import "./Klassifisering.less";

class Klassifisering extends Component {
  constructor(props) {
    super(props);
    let params = queryString.parse((this.props.location || {}).search);
    this.oppgaveId = params.oppgaveId || props.oppgaveId;
    this.state = {
      pdf: null,
      status: {},
      error: null,
      submitting: false,
      loading: true,
    };
  }

  async componentDidMount() {
    this.props.actions.resetKlassifisering();
    await ServiceklageApi.hentDokument(this.oppgaveId)
      .then((res) => {
        if (res.status === 204) {
          this.setState({ ...this.state, loading: false });
        } else {
          const dataUri = `data:application/pdf;base64,${res.data.dokument}`;
          this.setState({ ...this.state, pdf: dataUri, loading: false });
        }
      })
      .catch((err) =>
        this.setState({ ...this.state, error: err, loading: false })
      );

    if (!this.state.error && this.state.pdf) {
      await ServiceklageApi.hentKlassifiseringSkjema(this.oppgaveId)
        .then((res) => {
          this.props.actions.updateSchema({
            defaultAnswers: DefaultAnswersMapper(
              res.data.defaultAnswers || { answers: {} }
            ),
            questions: SchemaMapper(res.data.questions),
          });
        })
        .catch((err) => this.setState({ ...this.state, error: err }));
    }
  }

  async submitAnswers() {
    const { status, defaultAnswers } = this.props;
    const answers = {
      ...status.answers,
      ...Object.entries(defaultAnswers.answers || {}).reduce(
        (acc, [key, { answer }]) => ({
          ...acc,
          [key]: answer,
        }),
        {}
      ),
    };
    this.setState({ submitting: true });
    await ServiceklageApi.klassifiser(this.oppgaveId, answers)
      .then(() => (window.location = "/serviceklage/takk"))
      .catch((err) => this.setState({ error: err }));

    this.setState({ submitting: false });
  }

  render() {
    const { pdf, error, submitting, loading } = this.state;
    const { status } = this.props;
    return (
      <div className="Row">
        {pdf && !error && (
          <>
            <div className="Klassifisering">
              <h1>Skjema for klassifisering av serviceklager</h1>
              <SchemaRender />
              {status.progress.index === "none" &&
                (() => {
                  const answer = [...status.answersArray]
                    .reverse()
                    .find(answer => answer.button) || {
                    button: {
                      info: "Trykk fullfør for å ferdigstille oppgaven",
                      text: "Fullfør"
                    }
                  };
                  return (
                    <>
                      {answer.button.info && (
                        <AlertStripe type="info">
                          {answer.button.info}
                        </AlertStripe>
                      )}
                      <div className="SubmitButton">
                        <Hovedknapp onClick={() => this.submitAnswers()} disabled={submitting}>
                          {answer.button.text}
                        </Hovedknapp>
                      </div>
                    </>
                  );
                })()}
            </div>
            <div>
              <div className="Dokument">
                <Dokument pdf={pdf} />
              </div>
            </div>
          </>
        )}
        {!loading && !error && !pdf && (
          <div className={"Dokumentfeil"}>
            <h1>Dokument mangler</h1>
            <p>
              Det er ikke mulig å behandle en serviceklage når det ikke er et
              dokument tilknyttet oppgaven.
            </p>
            <h2>Du må:</h2>
            <ol>
              <li>
                Endre oppgavetype på den eksisterende oppgaven til "Vurder
                henvendelse"-oppgave. Ferdigstill denne oppgaven.
              </li>
              <li>
                Gå inn på journalposten til dokumentet i Gosys som inneholder
                serviceklagen. Opprett en ny "Vurder dokument"-oppgave derfra.
                Da kan du behandle serviceklagen.
              </li>
            </ol>
            <div className="GosysButton">
              <Hovedknapp
                onClick={() =>
                  (window.location = window.location.href.includes("preprod")
                    ? "https://gosys-q1.dev.intern.nav.no/gosys"
                    : "https://gosys.dev.intern.nav.no/gosys")
                }
              >
                Tilbake til Gosys
              </Hovedknapp>
            </div>
          </div>
        )}
        {error && (
          <div className={"Feilmelding"}>
            <h1>Beklager, det oppstod en feil!</h1>
            <p>{"Feilmelding: " + error.data.message}</p>
          </div>
        )}
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    answers: state.klassifiseringReducer.answers,
    defaultAnswers: state.klassifiseringReducer.defaultAnswers,
    status: state.klassifiseringReducer.status,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    actions: {
      updateSchema: ({ defaultAnswers, questions }) => {
        dispatch({
          type: UPDATE_SCHEMA,
          defaultAnswers,
          questions,
        });
      },
      resetKlassifisering: () => {
        dispatch({ type: RESET_KLASSIFISERING });
      },
    },
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Klassifisering);
