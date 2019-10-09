import React from 'react';
import {Route, Switch} from 'react-router-dom';
import FrontPage from "./pages/FrontPage";
import Klassifisering from "./pages/Klassifisering";
import Landingsside from "./pages/Landingsside";

const Routes = () => (
        <Switch>
            <Route exact path="/serviceklage/frontpage" component={FrontPage}/>
            <Route exact path="/serviceklage/klassifiser/:journalpostId" component={Klassifisering}/>
            <Route exact path="/serviceklage/takk" component={Landingsside}/>
        </Switch>
);

export default Routes;