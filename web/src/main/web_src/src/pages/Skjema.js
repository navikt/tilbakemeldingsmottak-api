import React, {Component, Fragment} from "react";
import RadioPanelGruppe from "nav-frontend-skjema/lib/radio-panel-gruppe";
import "./Klassifisering.less"
import Input from "nav-frontend-skjema/lib/input";
import Textarea from "nav-frontend-skjema/lib/textarea";
import CheckboksPanelGruppe from "nav-frontend-skjema/lib/checkboks-panel-gruppe";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import Select from "nav-frontend-skjema/lib/select";
import {ServiceklageApi} from "../api/Api";
import Modal from 'nav-frontend-modal';
import AlertStripe from "nav-frontend-alertstriper";
import Knapp from "nav-frontend-knapper/lib/knapp";


class Skjema extends Component {

    initState = {};

    constructor(props) {
        super(props);
        this.journalpostId = this.props.journalpostId;
        this.oppgaveId = this.props.oppgaveId;
        this.state = {
            erServiceklage: '',
            gjelder: '',
            kanal: '',
            paaklagetEnhet: '',
            behandlendeEnhet: '',
            ytelseTjeneste: '',
            tema: '',
            utfall: '',
            svarmetode: [],
            submitting: false,
            missingFieldsModalIsOpen: false,
            hasError: false,
            hidden: false
        };
        this.initState = {...this.state, svarmetode: []};
    }

    onChange = (event) => {
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

    onSubmit = async (event) => {
        event.preventDefault();
        await this.setState({...this.state, submitting: true});
        if ((this.state.erServiceklage.includes('Ja') && this.checkIsSet(this.state.paaklagetEnhet, this.state.behandlendeEnhet, this.state.ytelseTjeneste, this.state.tema, this.state.utfall, this.state.svarmetode))
            || (this.state.erServiceklage.includes('Nei') && this.checkIsSet(this.state.gjelder))) {
            try {
                await ServiceklageApi.klassifiserServiceklage(this.journalpostId, this.oppgaveId, this.state);
                await this.setState({...this.state, submitting: false});
                window.location = "/serviceklage/takk";
            } catch (error) {
                this.setState({...this.state, submitting: false, hasError: true});
                console.log(error);
            }
        } else {
            await this.setState({...this.state, missingFieldsModalIsOpen: true, submitting: false});
        }
    };

    onClickMissingFieldsModalButton = () => {
        this.setState({...this.state, missingFieldsModalIsOpen: false});
    };

    checkIsSet() {
        for (let i = 0; i < arguments.length; i++) {
            if (arguments[i] === '' || arguments[i] === []) {
                return false;
            }
        } return true;
    }

    render() {
        return (
            <Fragment>
                <h1>Klassifiser serviceklage</h1>
                <form>
                    <div className="Skjemafelt">
                        <div hidden={this.state.erServiceklage}>
                            <RadioPanelGruppe
                                name="erServiceklage"
                                legend="1. Er henvendelsen en serviceklage?"
                                radios={[
                                    { label: 'Ja (inkludert saker som også har andre elementer)', value: 'Ja (inkludert saker som også har andre elementer)'},
                                    { label: 'Nei - kun en forvaltningsklage', value: 'Nei - kun en forvaltningsklage'},
                                    { label: 'Nei - kun en beskjed til NAV', value: 'Nei - kun en beskjed til NAV'},
                                    { label: 'Nei - annet', value: 'Nei - annet'}
                                ]}
                                checked={this.state.erServiceklage}
                                onChange={this.onErServiceklageChange}
                            />
                        </div>

                        <div hidden={!this.state.erServiceklage}>
                            <legend className="skjema__legend">1. Er henvendelsen en serviceklage?</legend>
                            <p>{this.state.erServiceklage}</p>
                            <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, erServiceklage: ''})}>Endre</Knapp>
                        </div>
                    </div>

                    <div>
                        {this.state.erServiceklage.includes('Nei') &&
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
                        {this.state.erServiceklage.includes('Ja') &&
                        <Fragment>
                            <div className="Skjemafelt">
                                <div hidden={this.state.kanal}>
                                    <RadioPanelGruppe
                                        name="kanal"
                                        legend="2. Angi kanal for serviceklagen"
                                        radios={[
                                            { label: 'nav.no', value: 'nav.no'},
                                            { label: 'E-post', value: 'E-post'},
                                            { label: 'Brev', value: 'Brev'},
                                            { label: 'Muntlig (notat i Gosys fra NAV-medarbeider)', value: 'Muntlig (notat i Gosys fra NAV-medarbeider)'},
                                            { label: 'Modia', value: 'Modia'}
                                        ]}
                                        checked={this.state.kanal}
                                        onChange={this.onChange}
                                    />
                                </div>

                                <div hidden={!this.state.kanal}>
                                    <legend className="skjema__legend">2. Angi kanal for serviceklagen</legend>
                                    <p>{this.state.kanal}</p>
                                    <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, kanal: ''})}>Endre</Knapp>
                                </div>
                            </div>

                            <div className="Skjemafelt">
                                <legend className="skjema__legend">3. Angi enhetsnummer til enheten det klages på (4 siffer)</legend>
                                <Input
                                    label=""
                                    name="paaklagetEnhet"
                                    onChange={this.onChange}
                                    value={this.state.paaklagetEnhet}
                                    maxLength={4}
                                />
                            </div>

                            <div className="Skjemafelt">
                                <legend className="skjema__legend">4. Angi enhetsnummer til enhet som behandler klagen (4 siffer)</legend>
                                <Input
                                    label=""
                                    name="behandlendeEnhet"
                                    onChange={this.onChange}
                                    value={this.state.behandlendeEnhet}
                                    maxLength={4}
                                />
                            </div>

                            <div className="Skjemafelt">
                                <legend className="skjema__legend">5. Angi ytelse/tjeneste serviceklagen gjelder (velg det viktigste alternativet)</legend>
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
                                <div hidden={this.state.tema}>
                                    <RadioPanelGruppe
                                        name="tema"
                                        legend="6. Hva gjelder serviceklagen? Velg det viktigste temaet"
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

                                <div hidden={!this.state.tema}>
                                    <legend className="skjema__legend">6. Hva gjelder serviceklagen? Velg det viktigste temaet</legend>
                                    <p>{this.state.tema}</p>
                                    <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, tema: ''})}>Endre</Knapp>
                                </div>
                            </div>

                            {this.state.tema === 'Saksbehandling og svartid' &&
                                <div className="Skjemafelt">
                                    <div hidden={this.state.utfall}>
                                        <RadioPanelGruppe
                                            name="utfall"
                                            legend="7. Angi utfallet av serviceklagen"
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

                                    <div hidden={!this.state.utfall}>
                                        <legend className="skjema__legend">7. Angi utfallet av serviceklagen</legend>
                                        <p>{this.state.utfall}</p>
                                        <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, utfall: ''})}>Endre</Knapp>
                                    </div>
                                </div>
                            }

                            {this.state.tema === 'Veiledning, informasjon og oppfølging' &&
                            <div className="Skjemafelt">
                                <div hidden={this.state.utfall}>
                                    <RadioPanelGruppe
                                        name="utfall"
                                        legend="7. Angi utfallet av serviceklagen"
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

                                <div hidden={!this.state.utfall}>
                                    <legend className="skjema__legend">7. Angi utfallet av serviceklagen</legend>
                                    <p>{this.state.utfall}</p>
                                    <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, utfall: ''})}>Endre</Knapp>
                                </div>
                            </div>
                            }

                            {this.state.tema === 'Tilgjengelighet' &&
                            <div className="Skjemafelt">
                                <div hidden={this.state.utfall}>
                                    <RadioPanelGruppe
                                        name="utfall"
                                        legend="7. Angi utfallet av serviceklagen"
                                        radios={[
                                            { label: 'NAVs tilgjengelighet har vært for dårlig', value: 'NAVs tilgjengelighet har vært for dårlig'},
                                            { label: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner, men burde vært bedre tilpasset brukers behov', value: 'NAVs tilgjengelighet er i henhold til etatens vedtatte rutiner, men burde vært bedre tilpasset brukers behov'},
                                            { label: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner', value: 'NAVs tilgjengelighet er i henhold til enhetens vedtatte rutiner'},
                                            { label: 'Det er ikke mulig å spore hva som har skjedd', value: 'Det er ikke mulig å spore hva som har skjedd'},
                                        ]}
                                        checked={this.state.utfall}
                                        onChange={this.onChange}
                                    />
                                </div>

                                <div hidden={!this.state.utfall}>
                                    <legend className="skjema__legend">7. Angi utfallet av serviceklagen</legend>
                                    <p>{this.state.utfall}</p>
                                    <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, utfall: ''})}>Endre</Knapp>
                                </div>
                            </div>
                            }

                            {this.state.tema === 'NAV-ansattes oppførsel' &&
                            <div className="Skjemafelt">
                                <div hidden={this.state.utfall}>
                                    <RadioPanelGruppe
                                        name="utfall"
                                        legend="7. Angi utfallet av serviceklagen"
                                        radios={[
                                            { label: 'Erkjennes at NAV-ansatte var uforberedt til avtalt møte', value: 'Erkjennes at NAV-ansatte var uforberedt til avtalt møte'},
                                            { label: 'Erkjennes at NAV-ansattes språkbruk/oppførsel var uheldig/uønsket', value: 'Erkjennes at NAV-ansattes språkbruk/oppførsel var uheldig/uønsket'},
                                            { label: 'NAV-ansattes språkbruk/oppførsel vurderes som akseptabel', value: 'NAV-ansattes språkbruk/oppførsel vurderes som akseptabel'},
                                            { label: 'Det er ikke mulig å spore hvem som har hatt kontakt med bruker', value: 'Det er ikke mulig å spore hvem som har hatt kontakt med bruker'},
                                        ]}
                                        checked={this.state.utfall}
                                        onChange={this.onChange}
                                    />
                                </div>

                                <div hidden={!this.state.utfall}>
                                    <legend className="skjema__legend">7. Angi utfallet av serviceklagen</legend>
                                    <p>{this.state.utfall}</p>
                                    <Knapp onClick={e => e.preventDefault() || this.setState ( {...this.state, utfall: ''})}>Endre</Knapp>
                                </div>
                            </div>
                            }

                            {this.state.utfall !== '' &&
                            <div className="Skjemafelt">
                                <CheckboksPanelGruppe
                                    name="svarmetode"
                                    legend="8. Hvordan svares bruker? Velg ett eller flere alternativer"
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

                    <Hovedknapp spinner={this.state.submitting} htmlType="submit" onClick={this.onSubmit}>Lagre</Hovedknapp>

                    {this.state.hasError &&
                        <AlertStripe type="advarsel" className="Advarsel">Feil i kall til klassifiserServiceklage</AlertStripe>
                    }

                    <Modal
                        isOpen={this.state.missingFieldsModalIsOpen}
                        closeButton={false}
                        onRequestClose={this.onClickMissingFieldsModalButton}
                        contentLabel="Min modalrute"
                    >
                        <div style={{padding:'2rem 2.5rem'}}>
                            <p>Påkrevde felter er ikke satt</p>
                            <button onClick={this.onClickMissingFieldsModalButton}>Ok</button>
                        </div>
                    </Modal>

                </form>
            </Fragment>
        )
    }

}

export default Skjema