import React, { Component } from "react";
import Input from "./Input";

export default class DateInput extends Component {
  render() {
    return (
      <Input
        {...{
          ...this.props,
          properties: this.props.properties
            ? { ...this.props.properties, type: "date" }
            : { type: "date" }
        }}
      />
    );
  }
}
