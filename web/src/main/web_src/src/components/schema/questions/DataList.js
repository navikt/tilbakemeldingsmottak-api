import React, { Component } from "react";
import Select from "react-select";

export default class DataList extends Component {
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
