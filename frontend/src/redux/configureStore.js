import { createStore, applyMiddleware } from 'redux';
import authReducer from './authReducer';
import SecureLS from 'secure-ls';
import thunk from 'redux-thunk';
import { setAuthorizationHeader } from '../api/apiCalls';

const secureLs = new SecureLS();

const getStateFromStorage = () => {
    const appAuth = secureLs.get('sociapp-auth');

    let stateInLocalStorage = {
    isLoggedIn: false,
    username: undefined,
    password: undefined,
    displayName: undefined,
    image: undefined
    };

    if(appAuth) {
        stateInLocalStorage = appAuth;        
    }

    return stateInLocalStorage;
}

const updateStateInStorage = newState => {
    secureLs.set('sociapp-auth', newState);
}

const configureStore = () => {

    const initialState = getStateFromStorage();
    setAuthorizationHeader(initialState);
    const store =  createStore(authReducer, initialState, applyMiddleware(thunk));

    store.subscribe(() => {
        updateStateInStorage(store.getState());
        setAuthorizationHeader(store.getState());
    });

    return store;
}

  export default configureStore;