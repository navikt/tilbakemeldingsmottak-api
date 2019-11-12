import React, {Component} from "react";
import {Input as NavInput} from "nav-frontend-skjema";

export default class Input extends Component {
  render() {
    const { emit, emitValues, properties } = this.props;
    return (
        <NavInput
          label=""
          {...(properties || {})}
          onChange={event =>
            emit({ ...emitValues, answer: event.target.value })
          }
        />
    );
  }
}