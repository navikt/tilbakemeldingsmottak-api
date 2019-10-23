import React, { Component } from 'react'
import { Input as NavInput } from 'nav-frontend-skjema'

export default class Input extends Component {
  render() {
    const { onChange, next, title, question } = this.props

    return (
      <NavInput
        label={title}
        onChange={event =>
          onChange({
            answer: event.target.value === '' ? null : event.target.value,
            question: question,
            next: next
          })
        }
      />
    )
  }
}
