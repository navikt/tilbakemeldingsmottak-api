import React from 'react';
import {Route, Switch} from 'react-router-dom';
import FrontPage from "./pages/FrontPage";
import Tilbakemelding from "./pages/Tilbakemelding";
import Landingsside from "./pages/Landingsside";

const Routes = () => (
        <Switch>
            <Route exact path="/serviceklage/frontpage" component={FrontPage}/>
            <Route exact path="/serviceklage/tilbakemelding/:journalpostId" component={Tilbakemelding}/>
            <Route exact path="/serviceklage/takk" component={Landingsside}/>
        </Switch>
);

export default Routes;