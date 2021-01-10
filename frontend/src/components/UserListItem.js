import React from 'react';
import { Link } from 'react-router-dom';
import ProfileImageWithDefault from './ProfileImageWithDefault';

const UserListItem = (props) => {
    const { user } = props;
    const { username, displayName, image } = user;

    return (
        <Link to={`/user/${username}`} className="list-group-item list-group-item-action">
            <ProfileImageWithDefault className="rounded-circle" alt={`${username} profile`} image={image} width="32" height="32"/>
            <span className="pl-2">
                {displayName}@{username}
            </span>
        </Link> 
    );
};

export default UserListItem;