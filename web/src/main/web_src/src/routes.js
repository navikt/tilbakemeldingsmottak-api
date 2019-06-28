import React from 'react';
import {Route, Switch} from 'react-router-dom';
import FrontPage from "./pages/FrontPage";

const Routes = () => (
        <Switch>
            <Route exact path="/serviceklage/frontpage" component={FrontPage}/>
        </Switch>
);

export default Routes;