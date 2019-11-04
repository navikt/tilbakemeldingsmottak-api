import React, {Component} from "react";
import AlertStripe from "nav-frontend-alertstriper";

import DateInput from "./questions/DateInput";
import Input from "./questions/Input";
import RadioButtons from "./questions/RadioButtons";
import Select from "./questions/Select";
import TextArea from "./questions/TextArea";

export default class SchemaQuestion extends Component {
  static propTypes = {};

  static typemap = {
    date: DateInput,
    input: Input,
    radio: RadioButtons,
    select: Select,
    text: TextArea
  };

  static alertmap = {
    info: "info",
    success: "suksess",
    warn: "advarsel",
    error: "feil"
  };

  createAlert(answerIndex) {
    const { answers } = this.props;
    if (answers.length > answerIndex && answers[answerIndex].emit) {
      return (
        <div className="Skjemafelt">
          <AlertStripe
            type={SchemaQuestion.alertmap[answers[answerIndex].emit.type]}
          >
            {answers[answerIndex].emit.message}
          </AlertStripe>
        </div>
      );
    } else {
      return <></>;
    }
  }

  getAnswerEmitValues(question, defaultAnswer) {
    return {
      ...(question.answers
        ? (
            question.answers.find(
              answer => answer.answer === defaultAnswer.answer
            ) || question
          ).emitValues
        : question.emitValues),
      ...defaultAnswer
    };
  }

  createComponent() {
    const {
      question,
      questionIndex,
      answerIndex,
      answers,
      updateAnswer
    } = this.props;

    const Component = SchemaQuestion.typemap[question.type];
    const val = answers.length > answerIndex ? answers[answerIndex] : {};
    const numberOfDefaultAnswers = answers.reduce(
      (acc, answer, index) =>
        acc + (index < answerIndex && answer.default ? 1 : 0),
      0
    );
    const questionNumber = answerIndex + 1 - numberOfDefaultAnswers;
    return (
      <div key={questionIndex}>
        <div className="Skjemafelt">
          <legend className="skjema__legend">{`${questionNumber}) ${question.text}`}</legend>
          <Component
            {...question}
            index={questionIndex}
            value={val}
            emit={value => updateAnswer(value, answerIndex)}
          />
        </div>
        {this.createAlert(answerIndex)}
      </div>
    );
  }

  render() {
    const {
      question,
      questionIndex,
      answerIndex,
      answers,
      defaultAnswers,
      updateAnswer
    } = this.props;

    if (question.id && defaultAnswers.answers[question.id]) {
      if (answers.length <= answerIndex) {
        const values = {
          ...this.getAnswerEmitValues(
            question,
            defaultAnswers.answers[question.id]
          ),
          default: true
        };
        setTimeout(() => updateAnswer(values, answerIndex), 0);
      }
      return <div key={questionIndex} />;
    } else {
      return this.createComponent();
    }
  }
}
