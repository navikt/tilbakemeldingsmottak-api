import React, {Component} from "react";
import {Select as NavSelect} from "nav-frontend-skjema";

export default class Select extends Component {
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
          {...properties}
          label=""
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
