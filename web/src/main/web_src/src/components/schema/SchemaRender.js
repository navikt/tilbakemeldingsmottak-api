import React, { Component } from "react";
import { connect } from "react-redux";
import SchemaQuestion from "./SchemaQuestion";

class Render extends Component {
  constructor(props) {
    super(props);
    this.state = {
      answers: []
    };
  }

  componentDidUpdate() {
    this.el.scrollIntoView({ behavior: "smooth" });
  }

  collectEmitInfo() {
    const { answers } = this.props;
    this.props.onChange({
      progress: {
        index: answers.length >= 1 ? answers[answers.length - 1].next : 0,
        numberOfQuestions: this.props.questions.length - 1
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

  render() {
    let questions = this.props.answers
      .reduce((acc, answer) => [...acc, answer.next], [1])
      .filter(next => next && next !== "none")
      .map(next => next - 1);
    return (
      <>
        {questions
          .filter(questionIndex => this.props.questions.length > questionIndex)
          .map((questionIndex, answerIndex) => (
            <SchemaQuestion
              key={questionIndex}
              questionIndex={questionIndex}
              answerIndex={answerIndex}
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


const mapStateToProps = state => {
  return {
    questions: state.klassifiseringReducer.questions,
    answers: state.klassifiseringReducer.answers
  };
};

function mapDispatchToProps(dispatch) {
  return {}
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Render);
