import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { getContents, getNewContentCount, getNewContents, getOldContents } from '../api/apiCalls';
import { useApiProgress } from '../shared/ApiProgress';
import ContentView from './ContentView';
import Spinner from './Spinner';

const ContentFeed = () => {

    const [contentPage, setContentPage] = useState({content:[], last: true, number: 0});
    const { t } = useTranslation();
    const { username } = useParams();
    const [newContentCount, setNewContentCount] = useState(0);

    const path = username ? `/api/1.0/users/${username}/contents?page=` : '/api/1.0/contents?page=';

    let lastContentId = 0;
    let firstContentId = 0;
    if(contentPage.content.length > 0) {
        firstContentId = contentPage.content[0].id;
        const lastContentIndex = contentPage.content.length - 1;
        lastContentId = contentPage.content[lastContentIndex].id;
    }

    const initialContentLoadProgress = useApiProgress('get', path);

    const oldContentsPath = username ? `/api/1.0/users/${username}/contents/${lastContentId}` : `/api/1.0/contents/${lastContentId}`;

    const oldContentsLoadProgress = useApiProgress('get', oldContentsPath, true);

    const newContentsPath = username 
    ? `/api/1.0/users/${username}/contents/${firstContentId}?direction=after`
    : `/api/1.0/contents/${firstContentId}?direction=after`;

    const newContentsLoadProgress = useApiProgress('get', newContentsPath, true);

    useEffect(() => {
        const getCount = async () => {
            const response = await getNewContentCount(firstContentId, username);
            setNewContentCount(response.data.count);
        }

        let looper = setInterval(getCount, 5000);

        return function cleanUp() {
            clearInterval(looper);
        }
    }, [firstContentId, username]);

    useEffect(() => {
        const loadContents = async (page) => {
            try {
                const response = await getContents(username, page);
                setContentPage(previousContentPage => ({
                    ...response.data,
                    content: [...previousContentPage.content, ...response.data.content]
                }));
            } catch (error) {
    
            }
        };

        loadContents();
    }, []);    

    const loadOldContents = async () => {
        const response = await getOldContents(lastContentId, username);
        setContentPage(previousContentPage => ({
            ...response.data,
            content: [...previousContentPage.content, ...response.data.content]
        }));
    }

    const loadNewContents = async () => {
        const response = await getNewContents(firstContentId, username);
        setContentPage(previousContentPage => ({
            ...previousContentPage,
            content: [...response.data, ...previousContentPage.content]
        }));
        setNewContentCount(0);
    }

    const onDeleteContentSuccess = id => {
        setContentPage(previousContentPage => ({
            ...previousContentPage,
            content: previousContentPage.content.filter(content => content.id !== id)
        }));
    }

    const { content, last } = contentPage;

    if(content.length === 0) {
        return <div className="alert alert-secondary text-center">{initialContentLoadProgress ? <Spinner/> : t('No Content')}</div>
    }

    return (
        <div>
            {newContentCount > 0 && (
                <div 
                className="alert alert-secondary text-center mb-1" 
                style={{cursor: newContentsLoadProgress ? 'not-allowed' : 'pointer'}}
                onClick={newContentsLoadProgress ? () => {} : loadNewContents}
                >                    
                   {newContentsLoadProgress ? <Spinner/> : t('There are new contents') }
                </div>
            )}
            {content.map(eachContent => {
                return <ContentView key={eachContent.id} sharedContent = {eachContent} onDeleteContent={onDeleteContentSuccess}/>
            })}
            {!last && 
            <div 
            className="alert alert-secondary text-center" 
            style={{cursor: oldContentsLoadProgress ? 'not-allowed' : 'pointer'}} 
            onClick={oldContentsLoadProgress ? () => {} : loadOldContents}>
                {oldContentsLoadProgress ? <Spinner/> : t('Load Past Contents') }
            </div>
            }
        </div>
    );
};

export default ContentFeed;