import React, { useEffect, useRef, useState } from 'react';
import pica from '../assets/pica.png';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { logoutSuccess } from '../redux/authActions';
import ProfileImageWithDefault from './ProfileImageWithDefault';

const Navbar = () => {
    const { t } = useTranslation();

    const { username, isLoggedIn, displayName, image } = useSelector(store => ({
        isLoggedIn: store.isLoggedIn,
        username: store.username,
        displayName: store.displayName,
        image: store.image
    }));

    const menuArea = useRef(null);

    const [menuVisible, setMenuVisible] = useState(false);

    useEffect(() => {
        document.addEventListener('click', menuClickTracker);

        return () => {
            document.removeEventListener('click', menuClickTracker);
        }
    }, [isLoggedIn]);

    const menuClickTracker = (event) => {
        if(menuArea.current === null || !menuArea.current.contains(event.target)) {
            setMenuVisible(false);
        }
    };

    const dispatch = useDispatch();

    const onLogoutSuccess = () => {
        dispatch(logoutSuccess());
    };

    let links = (
        <ul className="navbar-nav ml-auto">    
            <li>
                <Link className="nav-link" to="/login">
                    {t('Login')}
                </Link>
            </li>
            <li>
                <Link className="nav-link" to="/register">
                {t('Sign Up')}
                </Link>
            </li>
        </ul> 
        );

        if(isLoggedIn) {

            let dropdownClass = "dropdown-menu p-0 shadow";

            if(menuVisible) {
                dropdownClass += ' show';
            }

            links = (
                <ul className="navbar-nav ml-auto" ref={menuArea}>
                    <li className="nav-item dropdown">
                        <div className="d-flex" style={{ cursor: 'pointer' }} onClick={() => setMenuVisible(true)}>
                            <ProfileImageWithDefault image={image} width="32" height="32" className="rounded-circle m-auto"/>
                            <span className="nav-link dropdown-toggle m-auto">{displayName}</span>
                        </div>
                        <div className={dropdownClass}>
                            <Link className="dropdown-item d-flex p-2" to={"/user/" + username} onClick={() => setMenuVisible(false)}>
                                <i className="material-icons text-info mr-2">person</i>
                                {t('My Profile')}
                            </Link>
                            <Link className="dropdown-item d-flex p-2" to="/" onClick={onLogoutSuccess}>
                                <i className="material-icons text-danger mr-2">power_settings_new</i>
                                {t('Logout')}
                            </Link>
                        </div>
                    </li> 
                </ul> 
            );
        }
        return (
            <div className="shadow-sm bg-light mb-2">
                <nav className="navbar navbar-light container navbar-expand">
                    <Link className="navbar-brand" to="/">
                        <img src={pica} width="60" alt="App Logo"/>
                        Sociapp
                    </Link>
                    {links}          
                </nav>
            </div>
        );
}

export default Navbar;
