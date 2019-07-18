import React, {Component, Fragment} from "react";
import RadioPanelGruppe from "nav-frontend-skjema/lib/radio-panel-gruppe";
import "./Tilbakemelding.less"
import Input from "nav-frontend-skjema/lib/input";
import Textarea from "nav-frontend-skjema/lib/textarea";
import CheckboksPanelGruppe from "nav-frontend-skjema/lib/checkboks-panel-gruppe";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import Select from "nav-frontend-skjema/lib/select";
import {ServiceKlageApi} from "../api/Api";

class Tilbakemelding extends Component {

    initState = {};

    constructor(props) {
        super(props);
        this.state = {
            erServiceklage: '',
            gjelder: '',
            paaklagetEnhet: '',
            behandlendeEnhet: '',
            ytelseTjeneste: '',
            tema: '',
            utfall: '',
            svarmetode: []
        };
        this.initState = {...this.state, svarmetode: []};
    }

    onChange = (event, ...rest) => {
        this.setState ( {...this.state, [event.target.name]: event.target.value });
    };

    onErServiceklageChange = (event) => {
        this.setState ( {...this.initState, svarmetode: [], [event.target.name]: event.target.value });
    };

    onTemaChange = (event) => {
        this.setState ( {...this.state, utfall:'', svarmetode: [], [event.target.name]: event.target.value });
    };

    onSvarmetodeChange = (event, value) => {
        if (this.state.svarmetode.includes(value)) {
            this.setState({...this.state, svarmetode: this.state.svarmetode.filter(svar => svar !== value)})
        } else {
            this.setState({...this.state, svarmetode: [...this.state.svarmetode, value]})
        }
    };

    onSubmit = (event) => {
        console.log("erServiceklage: " + this.state.erServiceklage);
        console.log("gjelder: " + this.state.gjelder);
        console.log("paaklagetEnhet: " + this.state.paaklagetEnhet);
        console.log("behandlendeEnhet: " + this.state.behandlendeEnhet);
        console.log("ytelseTjeneste: " + this.state.ytelseTjeneste);
        console.log("tema: " + this.state.tema);
        console.log("utfall: " + this.state.utfall);
        console.log("svarmetode: " + this.state.svarmetode);

        ServiceKlageApi.registrerTilbakemelding(7, this.state);
    };

    render() {
        return (
            <div className="Tilbakemelding">
                <h1>Tilbakemeldingsskjema serviceklager</h1>
                <form>
                    <div className="Skjemafelt">
                        <RadioPanelGruppe
                            name="erServiceklage"
                            legend="1. Er henvendelsen en serviceklage?"
                            radios={[
                                { label: 'Ja (inkludert saker som også har andre elementer)', value: 'ja'},
                                { label: 'Nei - kun en forvaltningsklage', value: 'nei_forvaltning'},
                                { label: 'Nei - annet', value: 'nei_annet'}
                            ]}
                            checked={this.state.erServiceklage}
                            onChange={this.onErServiceklageChange}
                        />
                    </div>

                    <div>
                        {(this.state.erServiceklage === 'nei_forvaltning' || this.state.erServiceklage === 'nei_annet') &&
                            <div className="Skjemafelt">
                                <legend className="skjema__legend">2. Kommenter hva henvendelsen gjaldt</legend>
                                <Textarea
                                    label=""
                                    name="gjelder"
                                    onChange={this.onChange}
                                    value={this.state.gjelder}
                                />
                            </div>
                        }
                    </div>

                    <div>
                        {this.state.erServiceklage === 'ja' &&
                        <Fragment>
                            <div className="Skjemafelt">
                                <legend className="skjema__legend">2. Angi enhetsnummer til enheten det klages på (4 siffer)</legend>
                                <Input
                                    label=""
                                    name="paaklagetEnhet"
                                    onChange={this.onChange}
                                    value={this.state.paaklagetEnhet}
                                    maxLength={4}
                                />
                            </div>

                            <div className="Skjemafelt">
                                <legend className="skjema__legend">3. Angi enhetsnummer til enhet som behandler klagen (4 siffer)</legend>
                                <Input
                                    label=""
                                    name="behandlendeEnhet"
                                    onChange={this.onChange}
                                    value={this.state.behandlendeEnhet}
                                    maxLength={4}
                                />
                            </div>

                            <div className="Skjemafelt">
                                <legend className="skjema__legend">4. Angi ytelse/tjeneste serviceklagen gjelder (velg det viktigste alternativet)</legend>
                                <Select label=""
                                        name="ytelseTjeneste"
                                        selected={this.state.ytelseTjeneste}
                                        onChange={this.onChange}>
                                        <option value=''>Velg ytelse/tjeneste</option>
                                        <option value='AFP - Avtalefestet pensjon'>AFP - Avtalefestet pensjon</option>
                                        <option value='Alderspensjon'>Alderspensjon</option>
                                        <option value='Arbeidsavklaringspenger'>Arbeidsavklaringspenger</option>
                                        <option value='Arbeidsmarkedsloven'>Arbeidsmarkedsloven</option>
                                        <option value='Barnebidragsområdet'>Barnebidragsområdet</option>
                                        <option value='Barnepensjon'>Barnepensjon</option>
                                        <option value='Barnetrygd'>Barnetrygd</option>
                                        <option value='Barns sykdom'>Barns sykdom</option>
                                        <option value='Bil'>Bil</option>
                                        <option value='Dagpenger'>Dagpenger</option>
                                        <option value='Eneforsørger'>Eneforsørger</option>
                                        <option value='Foreldrepenger'>Foreldrepenger</option>
                                        <option value='Gjenlevende ektefelle'>Gjenlevende ektefelle</option>
                                        <option value='Grunnstønad/hjelpestønad'>Grunnstønad/hjelpestønad</option>
                                        <option value='Hjelpemidler'>Hjelpemidler</option>
                                        <option value='Kontantstøtte'>Kontantstøtte</option>
                                        <option value='Medlemsskap'>Medlemsskap</option>
                                        <option value='Supplerende stønad'>Supplerende stønad</option>
                                        <option value='Sykepenger'>Sykepenger</option>
                                        <option value='Uførepensjon'>Uførepensjon</option>
                                        <option value='Yrkesskade/yrkessykdom'>Yrkesskade/yrkessykdom</option>
                                        <option value='Andre stønadsområder/lover/tjenester'>Andre stønadsområder/lover/tjenester</option>
                                        <option value='Uspesifisert'>Uspesifisert</option>
                                </Select>
                            </div>

                            <div className="Skjemafelt">
                                <RadioPanelGruppe
                                    name="tema"
                                    legend="5. Hva gjelder serviceklagen? Velg det viktigste temaet"
                                    radios={[
                                        { label: 'Saksbehandling og svartid', value: 'Saksbehandling og svartid'},
                                        { label: 'Veiledning, informasjon og oppfølging', value: 'Veiledning, informasjon og oppfølging'},
                                        { label: 'Tilgjengelighet', value: 'Tilgjengelighet'},
                                        { label: 'NAV-ansattes oppførsel', value: 'NAV-ansattes oppførsel'},
                                    ]}
                                    checked={this.state.tema}
                                    onChange={this.onTemaChange}
                                />
                            </div>

                            {this.state.tema === 'Saksbehandling og svartid' &&
                                <div className="Skjemafelt">
                                    <RadioPanelGruppe
                                        name="utfall"
                                        legend="6. Angi utfallet av serviceklagen"
                                        radios={[
                                            { label: 'Bruker har ikke fått svar innen frist', value: 'Bruker har ikke fått svar innen frist'},
                                            { label: 'Frist ikke passert, men bruker burde fått raskere svar', value: 'Frist ikke passert, men bruker burde fått raskere svar'},
                                            { label: 'Frist er ikke passert', value: 'Frist er ikke passert'},
                                            { label: 'Prøvd å komme i kontakt med bruker, men uten å lykkes', value: 'Prøvd å komme i kontakt med bruker, men uten å lykkes'},
                                            { label: 'Det er ikke mulig å spore hva som har skjedd', value: 'Det er ikke mulig å spore hva som har skjedd'},
                                        ]}
                                        checked={this.state.utfall}
                                        onChange={this.onChange}
                                    />
                                </div>
                            }

                            {this.state.tema === 'Veiledning, informasjon og oppfølging' &&
                            <div className="Skjemafelt">
                                <RadioPanelGruppe
                                    name="utfall"
                                    legend="6. Angi utfallet av serviceklagen"
                                    radios={[
                                        { label: 'Veiledning/informasjon/oppfølging har vært mangelfull/feil, samt brudd på etatens standarder og rutiner', value: 'Veiledning/informasjon/oppfølging har vært mangelfull/feil, samt brudd på etatens standarder og rutiner'},
                                        { label: 'Veiledning/informasjon/oppfølging har vært i henhold til standarder og rutiner, men burde likevel vært bedre tilpasser brukers behov', value: 'Veiledning/informasjon/oppfølging har vært i henhold til standarder og rutiner, men burde likevel vært bedre tilpasser brukers behov'},
                                        { label: 'Veiledning/informasjon/oppfølging har vært i henhold til standarder og rutiner', value: 'Veiledning/informasjon/oppfølging har vært i henhold til standarder og rutiner'},
                                        { label: 'Det er ikke mulig å spore hva som har skjedd', value: 'Det er ikke mulig å spore hva som har skjedd'},
                                    ]}
                                    checked={this.state.utfall}
                                    onChange={this.onChange}
                                />
                            </div>
                            }

                            {this.state.tema === 'Tilgjengelighet' &&
                            <div className="Skjemafelt">
                                <RadioPanelGruppe
                                    name="utfall"
                                    legend="6. Angi utfallet av serviceklagen"
                                    radios={[
                                        { label: 'NAVs tilgjengelighet har vært for dårlig', value: 'uforberedt'},
                                        { label: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner, men burde vært bedre tilpasset brukers behov', value: 'NAVs tilgjengelighet er i henhold til etatens vedtatte rutiner, men burde vært bedre tilpasset brukers behov'},
                                        { label: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner', value: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner'},
                                        { label: 'Det er ikke mulig å spore hva som har skjedd', value: 'Det er ikke mulig å spore hva som har skjedd'},
                                    ]}
                                    checked={this.state.utfall}
                                    onChange={this.onChange}
                                />
                            </div>
                            }

                            {this.state.tema === 'NAV-ansattes oppførsel' &&
                            <div className="Skjemafelt">
                                <RadioPanelGruppe
                                    name="utfall"
                                    legend="6. Angi utfallet av serviceklagen"
                                    radios={[
                                        { label: 'Erkjennes at NAV-ansatte var uforberedt til avtalt møte', value: 'uforberedt'},
                                        { label: 'Erkjennes at NAV-ansattes språkbruk/oppførsel var uheldig/uønsket', value: 'uheldig_atferd'},
                                        { label: 'NAV-ansattes språkbruk/oppførsel vurderes som akseptabel', value: 'akseptabel_atferd'},
                                        { label: 'Det er ikke mulig å spore hvem som har hatt kontakt med bruker', value: 'ikke_mulig_aa_spore'},
                                    ]}
                                    checked={this.state.utfall}
                                    onChange={this.onChange}
                                />
                            </div>
                            }

                            {this.state.utfall !== '' &&
                            <div className="Skjemafelt">
                                <CheckboksPanelGruppe
                                    name="svarmetode"
                                    legend="7. Hvordan svares bruker? Velg ett eller flere alternativer"
                                    checkboxes={[
                                        { label: 'Avtalt møte', value: 'Avtalt møte'},
                                        { label: 'Telefon', value: 'Telefon'},
                                        { label: 'Manuell post (brev)', value: 'Manuell post (brev)'},
                                        { label: 'E-post', value: 'E-post'},
                                        { label: 'Svar ikke nødvendig', value: 'Svar ikke nødvendig'},
                                        { label: 'Modia', value: 'Modia'},
                                    ]}
                                    checked={this.state.svarmetode}
                                    onChange={this.onSvarmetodeChange}
                                />
                            </div>
                            }

                        </Fragment>}
                    </div>

                    <Hovedknapp htmlType="submit" onClick={this.onSubmit}>Send tilbakemelding</Hovedknapp>
                </form>
            </div>
        )
    }
}

export default Tilbakemelding