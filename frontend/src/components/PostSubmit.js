import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useSelector } from 'react-redux';
import { postContent, postContentAttachment } from '../api/apiCalls';
import { useApiProgress } from '../shared/ApiProgress';
import ButtonWithProgress from './ButtonWithProgress';
import ProfileImageWithDefault from './ProfileImageWithDefault';
import Input from './Input';
import AutoUploadImage from './AutoUploadImage';

const HoaxSubmit = () => {

    const { image } = useSelector((store) => ({
        image: store.image
    }));

    const [focused, setFocused] = useState(false);
    const [post, setPost] = useState('');
    const { t } = useTranslation();
    const [errors, setErrors] = useState({});
    const [newImage, setNewImage] = useState();
    const [attachmentId, setAttachmentId] = useState();

    const pendingApiCall = useApiProgress('post', '/api/1.0/contents', true)
    const pendingFileUpload = useApiProgress('post', '/api/1.0/content-attachments', true);

    useEffect(() => {
        if(!focused) {
            setPost('');
            setErrors({});
            setNewImage();
            setAttachmentId();
        }
    }, [focused]);

    useEffect(() => {
        setErrors({});
    }, [post])

    const onClickShare = async () => {
        const body = {
            content: post,
            attachmentId: attachmentId
        }

        try {
            await postContent(body);
            setFocused(false);
        } catch (error) {
            if(error.response.data.validationErrors) {
                setErrors(error.response.data.validationErrors);
            }
        }
    };

    const onChangeFile = (event) => {
        if(event.target.files.length < 1) {
            return;
        }
        const file = event.target.files[0];
        const fileReader = new FileReader();
        fileReader.onloadend = () => {
            setNewImage(fileReader.result);
            uploadFile(file);
        };
        fileReader.readAsDataURL(file);
    };

    const uploadFile = async (file) => {
        const attachment = new FormData();
        attachment.append('file', file);
        const response = await postContentAttachment(attachment);
        setAttachmentId(response.data.id);
    };

    const {content: contentError} = errors;

    let textAreaClass = 'form-control'

    if(contentError) {
        textAreaClass += ' is-invalid'
    }

    return (
        <div className="card p-1 flex-row">
            <ProfileImageWithDefault image={image} width="32" height="32" className="rounded-circle mr-1" />
            <div className="flex-fill">
                <textarea
                className={textAreaClass} 
                rows={focused ? '3' : '1'} 
                onFocus={() => setFocused(true)}
                onChange = {event => setPost(event.target.value)}
                value = {post}
                />
                <div className="invalid-feedback">{contentError}</div>
                {
                focused && (
                <>
                    {!newImage && <Input type="file" onChange = {onChangeFile}/>}
                    {newImage && <AutoUploadImage image={newImage} uploading={pendingFileUpload}/>}
                    <div className = "text-right mt-1">
                        <ButtonWithProgress text={t('Share Thoughts')} pendingApiCall={pendingApiCall} className="btn btn-primary" onClick={onClickShare} disabled={pendingApiCall || pendingFileUpload} />
                            
                        <button disabled={pendingApiCall || pendingFileUpload} className="btn btn-light d-inline-flex ml-2" onClick={() => setFocused(false)}>
                                <i className="material-icons">close</i>
                                {t('Cancel')}
                        </button>
                    </div>
                </>
                )}    
            </div>
        </div>
    );
};

export default HoaxSubmit;