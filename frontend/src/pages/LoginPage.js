import React, { useEffect, useState } from 'react';
import Input from '../components/Input';
import { useTranslation } from 'react-i18next';
import { useApiProgress } from '../shared/ApiProgress';
import ButtonWithProgress from '../components/ButtonWithProgress';
import { useDispatch } from 'react-redux';
import { loginHandler } from '../redux/authActions';
 
const LoginPage = (props) => {

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();
    const [error, setError] = useState();

    const dispatch = useDispatch();

    useEffect(() => {
        setError(undefined);
    }, [username, password]);

    const onClickLogin = async event => {
        event.preventDefault();

        const { history } = props;
        const { push } = history;

        const creds = {
            username,
            password
        };

        try {
            await dispatch(loginHandler(creds));
            push('/');
        } catch (apiError) {
            setError(apiError.response.data.errorMessage);
        }
    }

    const { t } = useTranslation();

    const pendingApiCall = useApiProgress('post', '/api/1.0/auth');

    const buttonEnabled = username && password;

    return (
        <div className="container">
            <form>
                <h1 className="text-center">{t('Login')}</h1>
                    <Input name="username" label= {t('Username')} onChange={(event) => setUsername(event.target.value)}></Input>
                    <Input name="password" label= {t('Password')} onChange={(event) => setPassword(event.target.value)} type="password"></Input>
                    {error && <div className="alert alert-danger">
                        {error}
                    </div>}
                <div className="text-center">
                    <ButtonWithProgress 
                        onClick={onClickLogin}
                        disabled={pendingApiCall || !buttonEnabled}
                        pendingApiCall={pendingApiCall}
                        text={t('Login')}
                    />  
                </div>
            </form>
        </div>
    );
}

export default LoginPage;