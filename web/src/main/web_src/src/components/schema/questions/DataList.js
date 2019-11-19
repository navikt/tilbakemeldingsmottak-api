import React from "react";
import Select from "react-select";
import Question from "./QuestionComponent"

export default class DataList extends Question {
  constructor(props) {
    super(props);
    this.options = this.props.answers.map((answer, index) => ({
      value: index,
      label: answer.answer
    }));
  }

  render() {
    const { emit, answers, properties } = this.props;

    return (
      <Select
        placeholder=""
        {...properties}
        options={this.options}
        onChange={({ value, label }) => {
          emit({
            ...answers[value].emitValues,
            answer: label
          });
        }}
      />
    );
  }
}
