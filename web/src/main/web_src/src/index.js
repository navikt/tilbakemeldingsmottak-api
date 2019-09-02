import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux'
import {AppContainer} from 'react-hot-loader';
import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/variables.less'
import {ConnectedRouter} from 'connected-react-router'
import App from "./pages/App";
import Store, {history} from "./store";

export const store = Store();

export const render = target => {
    ReactDOM.render(
        <AppContainer>
            <Provider store={store}>
                <ConnectedRouter history={history}>
                    <App/>
                </ConnectedRouter>
            </Provider>
        </AppContainer>,
        target
    );
};

if (process.env.NODE_ENV !== "production" && process.env.WEB_COMPONENT === "true") {
    render(document.getElementById("root"))
}

if (module.hot) {
    module.hot.accept('./pages/App', () => {
        render()
    });
}