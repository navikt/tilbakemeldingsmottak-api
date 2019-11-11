import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {AppContainer} from "react-hot-loader";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles/variables.less";
import {ConnectedRouter} from "connected-react-router";
import App from "./pages/App";
import Store, {history} from "./store";

export const store = Store();
const target = document.getElementById("root");

const render = () => {
  ReactDOM.render(
    <AppContainer>
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <App />
        </ConnectedRouter>
      </Provider>
    </AppContainer>,
    target
  );
};

render();

if (module.hot) {
  module.hot.accept("./pages/App", () => {
    render();
  });
}
