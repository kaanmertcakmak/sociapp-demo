import React from 'react';
import logo192 from '../assets/logo192.png'

const ProfileImageWithDefault = (props) => {
    const { image, tempimage } = props;

    let imageSource = logo192;
    if(image) {
        imageSource = 'images/profile/' + image;
    }

    return (
        <img alt={`Profile`} 
        src={tempimage || imageSource} 
        { ... props} 
        onError={event => {
            event.target.src = {logo192};
        }}/>
    );
};

export default ProfileImageWithDefault;