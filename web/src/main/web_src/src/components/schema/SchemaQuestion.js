import React, {Component} from "react";
import {connect} from "react-redux";
import AlertStripe from "nav-frontend-alertstriper";

import { UPDATE_ANSWER } from "../../store/actions";
import DateInput from "./questions/DateInput";
import Input from "./questions/Input";
import RadioButtons from "./questions/RadioButtons";
import Select from "./questions/Select";
import TextArea from "./questions/TextArea";
import DataList from "./questions/DataList"
import Checkbox from "./questions/Checkbox";

class SchemaQuestion extends Component {
  static propTypes = {};

  static typemap = {
    checkbox: Checkbox,
    date: DateInput,
    input: Input,
    radio: RadioButtons,
    select: Select,
    text: TextArea,
    datalist: DataList
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

  createBanner(banner) {
    return (
      <>
        {banner && (
          <AlertStripe type={SchemaQuestion.alertmap[banner.type]}>{banner.message}</AlertStripe>
        )}
      </>
    );
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

  createComponent(question) {
    const { questionIndex, answerIndex, answers } = this.props;

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
          {this.createBanner(question.banner)}
          <Component
            {...question}
            index={questionIndex}
            value={val}
            onInit={() =>
              question.optional &&
              this.props.actions.updateAnswer(
                {
                  ...question.emitValues,
                  answer: null,
                  optional: question.optional
                },
                answerIndex
              )
            }
            emit={value =>
              this.props.actions.updateAnswer(
                {
                  ...value,
                  optional: question.optional
                },
                answerIndex
              )
            }
          />
        </div>
        {this.createAlert(answerIndex)}
      </div>
    );
  }

  render() {
    const {
      questions,
      questionIndex,
      answerIndex,
      answers,
      defaultAnswers
    } = this.props;

    const question = questions[questionIndex];

    if (question.id && defaultAnswers.answers[question.id]) {
      if (answers.length <= answerIndex) {
        const values = {
          ...this.getAnswerEmitValues(
            question,
            defaultAnswers.answers[question.id]
          ),
          default: true
        };
        setTimeout(
          () => this.props.actions.updateAnswer(values, answerIndex),
          0
        );
      }
      return <div key={questionIndex} />;
    } else {
      return this.createComponent(question);
    }
  }
}

const mapStateToProps = state => {
  return {
    defaultAnswers: state.klassifiseringReducer.defaultAnswers,
    questions: state.klassifiseringReducer.questions,
    answers: state.klassifiseringReducer.answers
  };
};

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      updateAnswer: (value, index) => {
        dispatch({
          type: UPDATE_ANSWER,
          value,
          index
        });
      }
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(SchemaQuestion);
