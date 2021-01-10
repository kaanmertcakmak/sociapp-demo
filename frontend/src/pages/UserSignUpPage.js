import React, { useState } from 'react';
import Input from '../components/Input';
import { useTranslation } from 'react-i18next';
import ButtonWithProgress from '../components/ButtonWithProgress';
import { useApiProgress } from '../shared/ApiProgress';
import { useDispatch } from 'react-redux';
import { signupHandler } from '../redux/authActions';

const UserSignUpPage = props => {

    const [form, setForm] = useState({
        username: null,
        displayName: null,
        password: null,
        confirmPassword: null
    });

    const [errors, setErrors] = useState({});

    const dispatch = useDispatch();

    const onChange = event => {
        const { name, value } = event.target;
        setErrors(previousError => ({ ...previousError, [name]:undefined }));
        setForm((previousForm) => ({
            ...previousForm,
            [name]: value
        }));
    }

    const onClickSignUp = async event => {
        event.preventDefault();

        const { history } = props;
        const { push } = history;

        const { username, displayName, password } = form;

        const body = {
            username,
            displayName,
            password
        };
        
        try {
            await dispatch(signupHandler(body));
            push('/');
        } catch (error) {
            if(error.response.data.validationErrors) {
                setErrors(error.response.data.validationErrors);
            }
        }
        
    }
        const {username: usernameErrors, displayName: displayNameErrors, password: passwordErrors} = errors;

        const pendingApiCallSignUp = useApiProgress('post', '/api/1.0/users');
        const pendingApiCallLogin = useApiProgress('post', '/api/1.0/auth');

        const pendingApiCall = pendingApiCallLogin || pendingApiCallSignUp;

        const { t } = useTranslation();

        let confirmPasswordErrors;
        if(form.password !== form.confirmPassword) {
            confirmPasswordErrors = t('Password Mismatch');
        }

        return (
            <div className="container">
                <form>
                    <h1 className="text-center">{t('Sign Up')}</h1>
                        <Input name="username" label= {t('Username')} error={usernameErrors} onChange={onChange}></Input>
                        <Input name="displayName" label= {t('Display Name')} error={displayNameErrors} onChange={onChange}></Input>
                        <Input name="password" label= {t('Password')} error={passwordErrors} onChange={onChange} type="password"></Input>
                        <Input name="confirmPassword" label= {t('Confirm Password')} error={confirmPasswordErrors} onChange={onChange} type="password"></Input>
                    <div className="text-center">
                        <ButtonWithProgress 
                            onClick={onClickSignUp}
                            disabled={pendingApiCall || confirmPasswordErrors !== undefined}
                            pendingApiCall={pendingApiCall}
                            text={t('Sign Up')}
                        />   
                    </div>                 
                </form>
            </div>                        
        );
    
}

export default UserSignUpPage;