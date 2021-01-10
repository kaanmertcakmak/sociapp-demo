import * as ACTIONS from './constants';

const defaultState = {
    isLoggedIn: false,
    username: undefined,
    password: undefined,
    displayName: undefined,
    image: undefined
  }
  
  const authReducer = (state = defaultState ,action) => {
    switch (action.type){
      case ACTIONS.LOGOUT_SUCCESS : {
        return defaultState;
      }
      case ACTIONS.LOGIN_SUCCESS : {
        return {isLoggedIn:true, ...action.payload}
      }
      case ACTIONS.UPDATE_SUCCESS : {
        return {
          ...state,
          ...action.payload
        }
      }
      default: {
        return state;
      }
    }
  }

  export default authReducer;