import * as ACTIONS from './constants';
import { login, logout, signUp } from '../api/apiCalls';

export const logoutSuccess = () => {
    return async function(dispatch) {
        try {
            await logout();
        } catch (err) {

        }
        dispatch({
            type: ACTIONS.LOGOUT_SUCCESS
        });
    }
};

export const loginSuccess = (authData) => {
    return {
        type: ACTIONS.LOGIN_SUCCESS,
        payload: authData
    };
};

export const updateSuccess = ({displayName, image}) => {
    return {
        type: ACTIONS.UPDATE_SUCCESS,
        payload: {
            displayName,
            image
        }
    }
}

export const loginHandler = (credentials) => {
    return async function (dispatch) {
        const response = await login(credentials);
        const authState = {
            ...response.data.userDto,
            password: credentials.password,
            token: response.data.token
        };
        dispatch(loginSuccess(authState));
        return response;
    };
};

export const signupHandler = (user) => {
    return async function (dispatch) {
        const response = await signUp(user);
        await dispatch(loginHandler(user));
        return response;
    };
};