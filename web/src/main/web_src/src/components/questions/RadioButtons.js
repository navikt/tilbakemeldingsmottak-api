import RadioPanelGruppe from "nav-frontend-skjema/lib/radio-panel-gruppe";
import React, {Component} from "react";

class RadioButtons extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className="Skjemafelt">
                <RadioPanelGruppe
                    name={this.props.legend}
                    legend={this.props.legend}
                    radios={this.props.alternatives.map(alternative => ({label: alternative.answer, value: alternative.answer}))}
                    checked={this.props.value}
                    onChange={this.props.onChange}
               />
            </div>
        )
    }
}

export default RadioButtons