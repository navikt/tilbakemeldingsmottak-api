import React from 'react';
import {Route, Switch} from 'react-router-dom';
import FrontPage from "./pages/FrontPage";
import Tilbakemelding from "./pages/Tilbakemelding";

const Routes = () => (
        <Switch>
            <Route exact path="/serviceklage/frontpage" component={FrontPage}/>
            <Route exact path="/serviceklage/tilbakemelding/:serviceklageId" component={Tilbakemelding}/>
        </Switch>
);

export default Routes;