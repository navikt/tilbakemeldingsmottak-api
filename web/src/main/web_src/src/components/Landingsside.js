import React, {Component} from "react";
import "./Landingsside.less"
import AlertStripe from "nav-frontend-alertstriper";

class Landingsside extends Component {

    constructor(props) {
        super(props)
    }

    render() {

        return (
            <div className="Landingsside">
                <h1>Klassifisering fullført</h1>
                <AlertStripe type="suksess">Serviceklagen er klassifisert og oppgaven har blitt lukket</AlertStripe>
            </div>
        )

    }

}

export default Landingsside