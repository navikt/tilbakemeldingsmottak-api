import React from "react";
import {Select as NavSelect} from "nav-frontend-skjema";
import Question from "./QuestionComponent"

export default class Select extends Question {
  constructor(props) {
    super(props);
    this.optionsMap = this.props.answers.reduce(
      (acc, option, index) => ({
        ...acc,
        [option.answer]: this.props.answers[index]
      }),
      {}
    );
    this.options = this.props.answers.map((answer, index) => (
      <option key={index}>{answer.answer}</option>
    ));
  }

  componentDidMount() {
    const { emit, answers } = this.props;
    emit({ ...answers[0].emitValues, answer: answers[0].answer });
  }

  render() {
    const { emit, properties } = this.props;
    const optionsMap = this.optionsMap;

    return (
        <NavSelect
          label=""
          {...properties}
          onChange={event =>
            emit({
              ...optionsMap[event.target.value].emitValues,
              answer: event.target.value
            })
          }
        >
          {this.options}
        </NavSelect>
    );
  }
}
