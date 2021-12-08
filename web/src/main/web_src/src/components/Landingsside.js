import React, {Component} from "react";
import "./Landingsside.less"
import AlertStripe from "nav-frontend-alertstriper";

class Landingsside extends Component {

    constructor(props) {
        super(props);
        this.redirectUrl = window.location.href.includes("preprod") ?
            "https://gosys-q1.dev.intern.nav.no/gosys/" :
            "https://gosys.intern.nav.no/gosys/"
    }

    componentDidMount() {
        setTimeout(() => window.location = this.redirectUrl, 3000)
    }

    render() {

        return (
            <div className="Landingsside">
                <h1>Skjema for klassifisering av serviceklager</h1>
                <AlertStripe type="suksess" className={"Alert"}>Utfylt skjema er sendt</AlertStripe>
                <p>Du blir videresendt til <a href={this.redirectUrl}>GOSYS</a> om tre sekunder.</p>
            </div>
        )

    }

}

export default Landingsside