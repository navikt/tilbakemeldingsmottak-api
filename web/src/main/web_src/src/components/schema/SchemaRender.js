import React, {Component} from "react";
import SchemaQuestion from "./SchemaQuestion";

export default class Render extends Component {
  constructor(props) {
    super(props);
    this.state = {
      answers: []
    };
    this.numberOfQuestions = this.props.schema.length;
  }

  componentDidUpdate() {
    this.el.scrollIntoView({ behavior: "smooth" });
  }

  collectEmitInfo() {
    const { answers } = this.state;
    this.props.onChange({
      progress: {
        index: answers.length >= 1 ? answers[answers.length - 1].next : 0,
        numberOfQuestions: this.numberOfQuestions - 1
      },
      answers: answers.reduce(
        (acc, answer) => ({
          ...acc,
          [answer.id]: answer.answer
        }),
        {}
      )
    });
  }

  createNewReferances(answers) {
    return answers.map(answer => ({
      ...answer,
      question: { ...answer.question }
    }));
  }

  updateArray(array, index, value) {
    if (array.length > index) {
      const copy = [...array];
      copy[index] = value;
      if (array[index].next !== copy[index].next) {
        return [...copy.slice(0, index), value];
      }
      return copy;
    } else {
      return [...array, value];
    }
  }

  updateAnswer = (() => {
    const el = this;
    return (value, index) => {
      el.setState(
        {
          ...el.state,
          answers: el.createNewReferances(
            value === null ||
              value === undefined ||
              value.answer == null ||
              value.answer === undefined
              ? el.state.answers.slice(0, index)
              : el.updateArray(el.state.answers, index, value)
          )
        },
        el.collectEmitInfo
      );
    };
  })();

  render() {
    let questions = this.state.answers
      .reduce((acc, answer) => [...acc, answer.next], [1])
      .filter(next => next && next !== "none")
      .map(next => next - 1);
    return (
      <>
        {questions
          .filter(questionIndex => this.props.schema.length > questionIndex)
          .map((questionIndex, answerIndex) => (
            <SchemaQuestion
              key={questionIndex}
              {...{
                answers: this.state.answers,
                question: this.props.schema[questionIndex],
                questionIndex,
                answerIndex,
                defaultAnswers: this.props.defaultAnswers,
                updateAnswer: this.updateAnswer
              }}
            />
          ))}
        <div
          ref={el => {
            this.el = el;
          }}
        />
      </>
    );
  }
}
