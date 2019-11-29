import React from "react";
import {default as NavTextArea} from "nav-frontend-skjema/lib/textarea";
import Question from "./QuestionComponent"

export default class TextArea extends Question {

  render() {
    const { emit, emitValues, value, properties } = this.props;
    return (
      <NavTextArea
        {...properties}
        label=""
        value={value && value.answer ? value.answer : ""}
        onChange={event =>
          emit({
            ...emitValues,
            answer: event.target.value === "" ? null : event.target.value
          })
        }
      />
    );
  }
}
