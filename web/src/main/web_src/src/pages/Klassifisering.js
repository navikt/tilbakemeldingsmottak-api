import React, {Component} from "react";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import queryString from "query-string";
import SchemaRender from "../components/schema/SchemaRender";
import {ServiceklageApi} from "../api/Api";
import {DefaultAnswersMapper, SchemaMapper} from "../mappers/skjema/SkjemaMapper";
import Dokument from "../components/Dokument";
import "./Klassifisering.less";


class Klassifisering extends Component {
  constructor(props) {
    super(props);
    let params = queryString.parse(this.props.location.search);
    this.journalpostId = params.journalpostId;
    this.oppgaveId = params.oppgaveId;
    this.state = {
      schema: null,
      pdf: null,
      status: {}
    };
  }

  componentDidMount() {
    ServiceklageApi.hentKlassifiseringSkjema(this.journalpostId).then(res =>
      this.setState({ ...this.state, schema: res.data })
    );

    ServiceklageApi.hentDokument(this.journalpostId).then(res => {
      const dataUri = `data:application/pdf;base64,${res.data.dokument}`;
      this.setState({ ...this.state, pdf: dataUri });
    });
  }

  render() {
    const { schema, pdf, status } = this.state;
    console.log(status);
    return (
      <div className="Row">
        <div className="Klassifisering">
          {schema && (
            <SchemaRender
              defaultAnswers={DefaultAnswersMapper(schema)}
              schema={SchemaMapper(schema)}
              onChange={status => this.setState({ ...this.state, status })}
            />
          )}
          {status.progress && status.progress.index === "none" && (
            <Hovedknapp>Lagre serviceklage og lukk oppgave</Hovedknapp>
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

export default Klassifisering;
