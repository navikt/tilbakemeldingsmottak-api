import {Component} from "react";

export default class Question extends Component {
  componentDidMount() {
    this.props.onInit();
  }
}
