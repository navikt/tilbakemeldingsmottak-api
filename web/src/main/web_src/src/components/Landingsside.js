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
                <h1>Skjema for klassifisering av serviceklager</h1>
                <AlertStripe type="suksess" className={"Alert"}>Ufylt skjema er sendt</AlertStripe>
                <p>Du blir videresendt til <a href={this.redirectUrl}>GOSYS</a> om tre sekunder.</p>
            </div>
        )

    }

}

export default Landingsside