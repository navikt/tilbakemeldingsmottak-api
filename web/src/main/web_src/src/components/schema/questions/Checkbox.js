import React from "react";
import Question from "./QuestionComponent"

import {CheckboksPanelGruppe} from "nav-frontend-skjema";

export default class Checkbox extends Question {
  constructor(props) {
    super(props);
    this.answers = [];
    this.props.onInit()
    this.checkboxes = this.props.answers.map(answer => ({
      value: answer.answer || "",
      id: answer.answer,
      label: answer.answer
    }));
  }

  render() {
    const { properties, emit, emitValues } = this.props;
    const th = this;

    return (
      <CheckboksPanelGruppe
        legend=""
        {...properties}
        checkboxes={this.checkboxes}
        onChange={(_, value) => {
          if (th.answers.includes(value)) {
            th.answers = th.answers.filter(val => val !== value);
          } else {
            th.answers = [...th.answers, value];
          }

          emit({
            ...emitValues,
            answer:
              th.answers.length > 0
                ? th.answers.reduce(
                    (acc, val) => `${acc}${acc ? "," : ""}${val}`,
                    ""
                  )
                : null
          });
        }}
      />
    );
  }
}
