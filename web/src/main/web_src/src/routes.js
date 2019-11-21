import React from 'react';
import {Route, Switch} from 'react-router-dom';
import Klassifisering from "./pages/Klassifisering";
import Landingsside from "./components/Landingsside";

const Routes = () => (
        <Switch>
            <Route exact path="/serviceklage/klassifiser" component={Klassifisering}/>
            <Route exact path="/serviceklage/takk" component={Landingsside}/>
        </Switch>
);

export default Routes;