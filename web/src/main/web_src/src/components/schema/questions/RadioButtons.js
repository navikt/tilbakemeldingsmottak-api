import RadioPanelGruppe from "nav-frontend-skjema/lib/radio-panel-gruppe";
import React, { Component } from "react";

class RadioButtons extends Component {
  constructor(props) {
    super(props);
    this.answerMap = this.props.answers.reduce(
      (acc, answer) => ({
        ...acc,
        [answer.answer]: answer
      }),
      {}
    );
    this.radios = this.props.answers.map(answer => ({
      label: answer.answer,
      value: answer.answer
    }));
  }

  render() {
    const { value, emit, text, properties } = this.props;
    const answerMap = this.answerMap;
    const radios = this.radios;

    return (
        <RadioPanelGruppe
          name={text}
          legend=""
          {...properties}
          radios={radios}
          checked={value && value.answer}
          onChange={event =>
            emit({
              answer: event.target.value,
              ...answerMap[event.target.value].emitValues
            })
          }
        />
    );
  }
}

export default RadioButtons;
