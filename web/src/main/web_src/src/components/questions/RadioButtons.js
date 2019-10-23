import RadioPanelGruppe from "nav-frontend-skjema/lib/radio-panel-gruppe";
import React, { Component } from "react";

class RadioButtons extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const { title, answers, value, onChange } = this.props;
    const answerMap = this.props.answers.reduce(
      (acc, answer) => ({
        ...acc,
        [answer.answer]: answer
      }),
      {}
    );

    return (
      <div className="Skjemafelt">
        <RadioPanelGruppe
          name={title}
          legend={title}
          radios={answers.map(answer => ({
            label: answer.answer,
            value: answer.answer
          }))}
          checked={value && value.answer}
          onChange={event => onChange(answerMap[event.target.value])}
        />
      </div>
    );
  }
}

export default RadioButtons;
