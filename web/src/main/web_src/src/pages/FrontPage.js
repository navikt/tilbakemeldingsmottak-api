import React, {Component, Fragment} from "react";
import {TitleComp} from "./TitleComp";
import {ServiceKlageApi} from "../api/Api";


class FrontPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            title: "test"
        }
    }

    async updateTitle(){
       let result = await ServiceKlageApi.ping();
        this.setState({
            title: result.data
        })
    }
    render() {

        return (
            <Fragment>
                <TitleComp title={this.state.title}/>
                <button onClick={() => this.updateTitle()}/>
            </Fragment>

        )

    }

}

export default FrontPage