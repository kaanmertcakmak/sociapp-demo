import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import ProfileImageWithDefault from './ProfileImageWithDefault';
import { format } from 'timeago.js';
import { useTranslation } from 'react-i18next';
import { useSelector } from 'react-redux';
import { deleteContent } from '../api/apiCalls';
import Modal from './Modal';
import { useApiProgress } from '../shared/ApiProgress';

const ContentView = (props) => {

    const loggedInUser = useSelector(store => store.username);

    const { sharedContent, onDeleteContent } = props

    const { user, content, timestamp, fileAttachment, id } = sharedContent;

    const { i18n, t } = useTranslation();

    const formattedTime = format(timestamp, i18n.language);

    const { username, displayName, image } = user;

    const ownedByLoggedInUser = loggedInUser === username;

    const [ modalVisible, setModalVisible ] = useState(false);

    const pendingApiCall = useApiProgress('delete', `/api/1.0/contents/${id}`, true);

    const onClickDeleteContent = async () => {
        await deleteContent(id);
        onDeleteContent(id);
        setModalVisible(false);
    }

    const onClickCancel = () => {
        setModalVisible(false);
    }

    return (
        <>
            <div className="card p-1">
                <div className="d-flex">
                    <ProfileImageWithDefault image={image} width="32" height="32" className="rounded-circle m-1"/>
                    <div className="flex-fill m-auto pl-2">
                        <Link to={`/user/${username}`} className="text-dark">
                            <h6 className="d-inline">
                                {displayName}@{username}
                            </h6>
                            <span> - </span>
                            <span>
                                {formattedTime}
                            </span>
                        </Link>
                    </div>
                    {ownedByLoggedInUser && <button className="btn btn-delete-link btn-sm" onClick={() => {setModalVisible(true);}}>
                        <i className="material-icons">delete_outline</i>
                    </button>}
                </div>
                <div className="pl-5">
                    {content} 
                </div>
                {fileAttachment && (
                    <div className="pl-5">
                        {fileAttachment.fileType.startsWith('image') && (
                            <img className="img-fluid" src={'images/attachments/' + fileAttachment.name} alt={content} />
                        )}
                        {!fileAttachment.fileType.startsWith('image') && (
                            <strong>{t('Content has unknown attachment!')}</strong>
                        )}                     
                    </div>
                )}
            </div>
            <Modal 
            visible={modalVisible}
            onClickCancel={onClickCancel}
            onClickOk={onClickDeleteContent}
            message={
                <div>
                    <div>
                        <strong>{t('Are you sure to delete following post?')}</strong>
                    </div>
                    <span>{content}</span>
                </div>
            }
            pendingApiCall= {pendingApiCall}
            title={t('Delete Post')}
            buttonText={t('Delete Post')}
            buttonId="deleteContent"
            />
        </ >
    );
};

export default ContentView;