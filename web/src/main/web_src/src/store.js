import {applyMiddleware, combineReducers, createStore} from 'redux';
import thunkMiddleware from 'redux-thunk';
import {composeWithDevTools} from 'redux-devtools-extension/developmentOnly';
import reduxImmutableStateInvariant from 'redux-immutable-state-invariant';
import {createBrowserHistory} from "history";
import {connectRouter, routerMiddleware} from 'connected-react-router'

export const history = createBrowserHistory();
const appReducer= combineReducers({
    router: connectRouter(history),
});



const Store = () => {
    const reduxImmutableStateInvariantMiddleware = reduxImmutableStateInvariant();
    const allMiddleware = [
        routerMiddleware(history),
        reduxImmutableStateInvariantMiddleware,
        thunkMiddleware,
    ];

    // Add redux logger if not in production
    if (process.env.NODE_ENV !== `production`) {
        const createLogger = require(`redux-logger`).createLogger
        const logger = createLogger({collapsed: true})
        allMiddleware.push(logger)
    }


    const store = createStore(
        appReducer,
        composeWithDevTools(applyMiddleware(...allMiddleware))
    );
    return store;
}


export default Store;
