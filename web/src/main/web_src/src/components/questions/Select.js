import React, { Component } from 'react'
import { Select as NavSelect } from 'nav-frontend-skjema'

export default class Select extends Component {
  componentDidMount() {
    const { onChange, answers } = this.props
    onChange(answers[0])
  }

  render() {
    const { title, onChange, answers } = this.props

    const options = answers.reduce(
      (acc, option, index) => ({ ...acc, [option.answer]: answers[index] }),
      {}
    )

    return (
      <NavSelect
        label={title}
        onChange={event => {
          onChange(options[event.target.value])
        }}
      >
        {answers.map(object => 
            <option key={options[object.answer].answer}>{object.answer}</option>
          )
       }
      </NavSelect>
    )
  }
}