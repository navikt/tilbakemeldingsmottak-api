import React, {Component} from "react";
import {ServiceklageApi} from "../api/Api";
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
            serviceklage: null,
            journalpostId: '',
            serviceklageHentetFlag: false
        }

    }

    async hentServiceklage(){
       let serviceklage = await ServiceklageApi.hentServiceklage(this.state.journalpostId);
        this.setState({
            serviceklage: serviceklage.data,
            serviceklageHentetFlag: true
        });
        this.render();
    }

    velgServiceklage = (id) => {
        window.location = "/serviceklage/klassifiser?journalpostId=" + id;
    };

   onChange = (event) => {
        this.setState ( {...this.state, [event.target.name]: event.target.value });
    };

    render() {

        return (
            <div className="ListView">
                <h1>Serviceklagesøk</h1>
                <Input
                    label="Skriv inn journalpost-id:"
                    name="journalpostId"
                    onChange={this.onChange}
                    value={this.state.journalpostId}
                />
                {this.state.serviceklageHentetFlag && !this.state.serviceklage &&
                    <div className="Advarsel"><AlertStripe type="advarsel">Det finnes ingen serviceklager knyttet til denne journalpost-iden.</AlertStripe></div>}

                <Hovedknapp onClick={() => this.hentServiceklage()}>Hent serviceklage</Hovedknapp>

                {this.state.serviceklage &&
                    <div className="Serviceklage">
                        {this.state.serviceklage.erServiceklage && <div className="Advarsel"><AlertStripe type="advarsel">Serviceklagen er allerede klassifisert.</AlertStripe></div>}
                        <Serviceklage
                            klagenGjelderId={this.state.serviceklage.klagenGjelderId}
                            paaVegneAv={this.state.serviceklage.paaVegneAv}
                            datoOpprettet={this.state.serviceklage.datoOpprettet}
                            klagetype={this.state.serviceklage.klagetype}
                            klagetekst={this.state.serviceklage.klagetekst}
                            erServiceklage={this.state.serviceklage.erServiceklage}
                            gjelder={this.state.serviceklage.gjelder}
                            utfall={this.state.serviceklage.utfall}
                            svarmetode={this.state.serviceklage.svarmetode}/>
                        <Knapp disabled={!!this.state.serviceklage.erServiceklage} onClick={() => this.velgServiceklage(this.state.serviceklage.journalpostId)}>Klassifiser</Knapp>
                    </div>}
            </div>

        )

    }

}

export default FrontPage