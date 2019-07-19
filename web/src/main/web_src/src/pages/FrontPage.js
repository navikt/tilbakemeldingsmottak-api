import React, {Component} from "react";
import {ServiceKlageApi} from "../api/Api";
import "./FrontPage.less"
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import Input from "nav-frontend-skjema/lib/input";
import {Serviceklage} from "../components/Serviceklage";
import Knapp from "nav-frontend-knapper/lib/knapp";
import AlertStripe from "nav-frontend-alertstriper";

class FrontPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            serviceklager: [],
            brukerId: '',
            serviceklagerHentetFlag: false
        }

    }

    async hentServiceklager(){
       let serviceklageList = await ServiceKlageApi.hentServiceklager(this.state.brukerId);
        this.setState({
            serviceklager: serviceklageList.data.serviceklager,
            serviceklagerHentetFlag: true
        });
        this.render();
    }

    velgServiceklage = (id) => {
        window.location = "/serviceklage/tilbakemelding/" + id;
    };

   onChange = (event) => {
        this.setState ( {...this.state, [event.target.name]: event.target.value });
    };

    render() {

        return (
            <div className="ListView">
                <h1>Serviceklagesøk</h1>
                <Input
                    label="Skriv inn bruker-id (fødselsnummer eller organisasjonsnummer):"
                    name="brukerId"
                    onChange={this.onChange}
                    value={this.state.brukerId}
                />
                {this.state.serviceklagerHentetFlag && this.state.serviceklager.length === 0 &&
                    <div className="Advarsel"><AlertStripe type="advarsel">Det finnes ingen serviceklager knyttet til denne bruker-iden.</AlertStripe></div>}

                <Hovedknapp onClick={() => this.hentServiceklager()}>Hent serviceklager</Hovedknapp>

                {this.state.serviceklager.map((s) =>
                    <div className="Serviceklage">
                        <Serviceklage
                            serviceklageId={s.serviceklageId}
                            datoOpprettet={s.datoOpprettet}
                            klagetype={s.klagetype}
                            klagetekst={s.klagetekst}
                            oenskerAaKontaktes={s.oenskerAaKontaktes ? "Ja" : "Nei"}
                            erBehandlet={s.erBehandlet ? "Ja" : "Nei"}/>
                        <Knapp onClick={() => this.velgServiceklage(s.serviceklageId)}>Registrer tilbakemelding</Knapp>
                    </div>
                )}
            </div>

        )

    }

}

export default FrontPage