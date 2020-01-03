import React, { Component } from "react";
import { connect } from "react-redux";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import AlertStripe from "nav-frontend-alertstriper";
import queryString from "query-string";
import SchemaRender from "../components/schema/SchemaRender";
import { ServiceklageApi } from "../api/Api";
import {
  DefaultAnswersMapper,
  SchemaMapper
} from "../mappers/skjema/SkjemaMapper";
import Dokument from "../components/Dokument";
import { RESET_KLASSIFISERING, UPDATE_SCHEMA } from "../store/actions";
import "./Klassifisering.less";

class Klassifisering extends Component {
  constructor(props) {
    super(props);
    let params = queryString.parse((this.props.location || {}).search);
    this.oppgaveId = params.oppgaveId || props.oppgaveId;
    this.state = {
      pdf: null,
      status: {},
      error: null
    };
  }

  async componentDidMount() {
    this.props.actions.resetKlassifisering();
    await ServiceklageApi.hentDokument(this.oppgaveId)
      .then(res => {
        const dataUri = `data:application/pdf;base64,${res.data.dokument}`;
        this.setState({ ...this.state, pdf: dataUri });
      })
      .catch(err => this.setState({ ...this.state, error: err }));

    if (!this.state.error) {
      await ServiceklageApi.hentKlassifiseringSkjema(this.oppgaveId)
        .then(res => {
          this.props.actions.updateSchema({
            defaultAnswers: DefaultAnswersMapper(
              res.data.defaultAnswers || { answers: {} }
            ),
            questions: SchemaMapper(res.data.questions)
          });
        })
        .catch(err => this.setState({ ...this.state, error: err }));
    }
  }

  async submitAnswers() {
    const { status, defaultAnswers } = this.props;
    const answers = {
      ...status.answers,
      ...Object.entries(defaultAnswers.answers || {}).reduce(
        (acc, [key, { answer }]) => ({
          ...acc,
          [key]: answer
        }),
        {}
      )
    };
    await ServiceklageApi.klassifiser(this.oppgaveId, answers)
      .then(() => (window.location = "/serviceklage/takk"))
      .catch(err => this.setState({ error: err }));
  }

  render() {
    const { pdf, error } = this.state;
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
                      text: "Trykk fullfør for å ferdigstille oppgaven"
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
                        <Hovedknapp onClick={() => this.submitAnswers()}>
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

const mapStateToProps = state => {
  return {
    answers: state.klassifiseringReducer.answers,
    defaultAnswers: state.klassifiseringReducer.defaultAnswers,
    status: state.klassifiseringReducer.status
  };
};

const mapDispatchToProps = dispatch => {
  return {
    actions: {
      updateSchema: ({ defaultAnswers, questions }) => {
        dispatch({
          type: UPDATE_SCHEMA,
          defaultAnswers,
          questions
        });
      },
      resetKlassifisering: () => {
        dispatch({ type: RESET_KLASSIFISERING });
      }
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Klassifisering);
