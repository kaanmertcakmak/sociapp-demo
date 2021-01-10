import React from 'react';
import { useSelector } from 'react-redux';
import ContentFeed from '../components/ContentFeed';
import PostSubmit from '../components/PostSubmit';
import UserList from '../components/UserList'

const HomePage = () => {

    const { isLoggedIn } = useSelector(store => ({ isLoggedIn: store.isLoggedIn }));

    return (
        <div className="container">
            <div className="row">
                <div className="col">
                    {isLoggedIn && 
                    <div className="mb-1">
                        <PostSubmit />
                    </div>
                    }
                    <ContentFeed />
                </div>
                <div className="col">
                    <UserList/>           
                </div>
            </div>
        </div>
    );
};

export default HomePage;