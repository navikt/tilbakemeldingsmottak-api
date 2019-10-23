import React, {Component} from "react";
import RadioButtons from "./questions/RadioButtons"
import Select from "./questions/Select"
import Input from './questions/Input'

export default class Render extends Component {
  constructor(props) {
    super(props)
    this.state = {
      answers: []
    }
  }

  static typemap = {
    radio: RadioButtons,
    input: Input,
    select: Select
  }

  updateArray(array, index, value) {
    if (array.length > index) {
      const copy = [...array]
      copy[index] = value
      if (array[index].next !== copy[index].next) {
        return [...copy.slice(0, index), value]
      }
      return copy
    } else {
      return [...array, value]
    }
  }

  updateAnswer(value, index) {
    this.setState({
      ...this.state,
      answers:
        value === null || value === undefined || value.answer == null || value.answer === undefined
          ? this.state.answers.slice(0, index)
          : this.updateArray(this.state.answers, index, value)
    })
  }

  createComponent(question, answerIndex) {
    const Component = Render.typemap[question.type]
    const { answers } = this.state
    const val = answers.length > answerIndex ? answers[answerIndex] : {}

    return (
        <Component
           key={answerIndex}
          {...question}
          value={val}
          onChange={value => this.updateAnswer(value, answerIndex)}
        />
    )
  }

  render() {
    let questions = this.state.answers
      .reduce((acc, answer) => [...acc, answer.next], [1])
      .filter(next => next && next !== 'none')
      .map(next => next - 1)

    return questions
      .filter(questionIndex => this.props.schema.length > questionIndex)
      .map((questionIndex, answerIndex) =>
        this.createComponent(this.props.schema[questionIndex], answerIndex)
      )
  }
}

