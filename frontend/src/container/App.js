import React from 'react';
import LoginPage from '../pages/LoginPage';
import UserSignUpPage from '../pages/UserSignUpPage'
import HomePage from '../pages/HomePage';
import UserPage from '../pages/UserPage';
import LanguageSelector from '../components/LanguageSelector';
import Navbar from '../components/Navbar';
import { HashRouter as Router, Route, Redirect, Switch } from 'react-router-dom';
import { useSelector } from 'react-redux';

const App = () => {

  const { isLoggedIn } = useSelector(store => ({
    isLoggedIn: store.isLoggedIn
  }));

    return (
      <div>
        <Router>
          <Navbar />
          <Switch>
            <Route exact path="/" component={HomePage}/>
            {!isLoggedIn && (<Route path="/login" component={LoginPage}/>)}
            <Route path="/register" component={UserSignUpPage}/>
            <Route path="/user/:username" component={UserPage}/>
            <Redirect to="/"/>
          </Switch>
        </Router>
        <LanguageSelector/>
      </div>
    );  
}

export default App;
