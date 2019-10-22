import React, {Component} from "react";
import "./Klassifisering.less"
import * as queryString from 'query-string';
import SkjemaRender from "../components/SkjemaRender";
import Dokument from "../components/Dokument";

const skjema = {
    questions: [
        {
            "question": "test question",
            "legend": "test1",
            "type": {
                "type": "radio"
            },
            "alternatives": [
                {
                    "answer": "1",
                    "questions": [
                        {

                        }
                    ]
                },
                {
                    "answer": "2"
                },
                {
                    answer: "3"
                }
            ],
            props: {
            },
        },
        {
            "question": "test question",
            "legend": "test1",
            "type": {
                "type": "radio"
            },
            "alternatives": [
                {
                    "answer": "1",
                    "questions": [
                        {

                        }
                    ]
                },
                {
                    "answer": "2"
                },
                {
                    answer: "3"
                }
            ],
            props: {
            },
        },

    ]
}


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
                    <SkjemaRender schema={skjema} journalpostId={this.journalpostId} oppgaveId={this.oppgaveId}/>
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