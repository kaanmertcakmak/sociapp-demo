import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { getUser } from '../api/apiCalls';
import ContentFeed from '../components/ContentFeed';
import ProfileCard from '../components/ProfileCard';
import Spinner from '../components/Spinner';
import { useApiProgress } from '../shared/ApiProgress';

const UserPage = () => {
    const [user, setUser] = useState({});
    const [notFound, setNotFound] = useState(false);

    const { username } = useParams();
    const { t } = useTranslation();

    const pendingApiCall = useApiProgress('get', '/api/1.0/users/' + username, true);

    useEffect(() => {
        const loadUser = async () => {
            try {
                const response = await getUser(username);
                setUser(response.data);
                setNotFound(false);
            } catch (error) {
                setNotFound(true);
            }
        };
        loadUser();
    }, [username]);

    if(notFound) {
        return (
            <div className="container">
                <div className="alert alert-danger text-center" role="alert">
                    <div>
                    <i className="material-icons" style={{fontSize: '48px'}}>
                    error_outline
                    </i>
                    </div>
                    {t('User Not Found')}
                </div>
            </div>
        )
    }

    if (pendingApiCall || user.username !== username) {
        return <Spinner/>;
    }

    return (
        <div className="container">
            <div className="row">
                <div className="col">
                    <ProfileCard user={user}/>         
                </div>
                <div className="col">
                    <ContentFeed />
                </div>
            </div>
        </div>
    );
};

export default UserPage;