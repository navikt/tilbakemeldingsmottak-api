import React from "react";
import {Input as NavInput} from "nav-frontend-skjema";
import Question from "./QuestionComponent"

export default class Input extends Question {
  componentDidMount() {
    this.props.onInit();
  }
  render() {
    const { emit, emitValues, properties } = this.props;
    return (
      <NavInput
        label=""
        {...(properties || {})}
        onChange={event => emit({ ...emitValues, answer: event.target.value })}
      />
    );
  }
}
