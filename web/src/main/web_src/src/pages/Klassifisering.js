import React, {Component} from "react";
import {connect} from "react-redux";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
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
      status: {}
    };
  }

  componentDidMount() {
    this.props.actions.resetKlassifisering();
    ServiceklageApi.hentKlassifiseringSkjema(this.oppgaveId).then(res => {
      this.props.actions.updateSchema({
        defaultAnswers: DefaultAnswersMapper(res.data.defaultAnswers) || {answers: {}},
        questions: SchemaMapper(res.data.questions)
      });
    });

    ServiceklageApi.hentDokument(this.oppgaveId)
      .then(res => {
        const dataUri = `data:application/pdf;base64,${res.data.dokument}`;
        this.setState({ ...this.state, pdf: dataUri });
      })
      .catch(console.error);
  }

  submitAnswers() {
    const { status, defaultAnswers } = this.props;
    const answers = {
        ...status.answers,
        ...Object.entries(defaultAnswers.answers || {}).reduce((acc, [key, {answer}]) => ({
            ...acc,
            [key]: answer
        }), {})
    };
    ServiceklageApi.klassifiser(this.oppgaveId, answers);
  }

  render() {
    const { pdf } = this.state;
    const { status } = this.props;
    return (
      <div className="Row">
        <div className="Klassifisering">
          <SchemaRender />
          {status.progress.index === "none" && (
            <div className="SubmitButton">
              <Hovedknapp onClick={() => this.submitAnswers()}>
                Lagre serviceklage og lukk oppgave
              </Hovedknapp>
            </div>
          )}
        </div>
        <div>
          <div className="Dokument">
            <Dokument pdf={pdf} />
          </div>
        </div>
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

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Klassifisering);
