import React, {Component} from "react";
import "./Landingsside.less"
import AlertStripe from "nav-frontend-alertstriper";

class Landingsside extends Component {

    constructor(props) {
        super(props);
        this.redirectUrl = window.location.href.includes("preprod") ?
            "https://gosys-nais-q1.nais.preprod.local/gosys/" :
            "https://gosys-nais.nais.adeo.no/gosys/"
    }

    componentDidMount() {
        setTimeout(() => window.location = this.redirectUrl, 3000)
    }

    render() {

        return (
            <div className="Landingsside">
                <h1>Klassifisering fullført</h1>
                <AlertStripe type="suksess" className={"Alert"}>Serviceklagen er klassifisert og oppgaven har blitt lukket</AlertStripe>
                <p>Du blir videresendt til <a href={this.redirectUrl}>GOSYS</a> om fem sekunder.</p>
            </div>
        )

    }

}

export default Landingsside