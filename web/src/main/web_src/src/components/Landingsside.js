import React, {Component} from "react";
import "./Landingsside.less"
import AlertStripe from "nav-frontend-alertstriper";

class Landingsside extends Component {

    constructor(props) {
        super(props)
    }

    componentDidMount() {
        setTimeout(window.location = "https://gosys-nais-q1.nais.preprod.local/gosys/", 3000)
    }

    render() {

        return (
            <div className="Landingsside">
                <h1>Klassifisering fullført</h1>
                <AlertStripe type="suksess">Serviceklagen er klassifisert og oppgaven har blitt lukket</AlertStripe>
                <p>Du blir videresendt til <a href={"https://gosys-nais-q1.nais.preprod.local/gosys/"}>gosys</a> om tre sekunder.</p>

                {}

            </div>
        )

    }

}

export default Landingsside