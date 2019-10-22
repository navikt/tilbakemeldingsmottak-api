import React, {Component} from "react";
import "./Klassifisering.less"
import * as queryString from 'query-string';
import Skjema from "./Skjema";
import Dokument from "./Dokument";


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
                    <Skjema journalpostId={this.journalpostId} oppgaveId={this.oppgaveId}/>
                </div>

                <div>
                    <div className="Dokument">
                        <Dokument />
                    </div>
                </div>
            </div>
        )
    }

}

export default Klassifisering