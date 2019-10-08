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
                <AlertStripe type="suksess">Serviceklagen er klassifisert</AlertStripe>
            </div>
        )

    }

}

export default Landingsside