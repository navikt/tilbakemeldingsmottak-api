import React, {Component} from "react";
import RadioButtons from "./questions/RadioButtons"

export default class SkjemaRender extends Component {
    constructor(props) {
        super(props);
        this.schema = this.normalizeSchema(this.props.schema);
        this.typemap = {
            radio: RadioButtons
        }
    }

    normalizeSchema(schema) {
        return schema.questions.map(question => ({
            ...question,
            props: {...(question.props || {}), alternatives: question.alternatives, legend: question.legend}
        }));
    }

    renderQuestion(type, props, question) {
        const Question = this.typemap[type.type];
        return <Question {...props}/>
    }

    render() {
        const schema = this.schema;
        return schema.map(question => this.renderQuestion(question.type, question.props, question));
    }
}
