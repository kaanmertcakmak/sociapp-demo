import axios from 'axios';

export const signUp = (body) => {
    return axios.post('/api/1.0/users', body);
};

export const login = (creds) => {
    return axios.post('/api/1.0/auth', creds);
};

export const logout = () => {
    return axios.post('/api/1.0/logout');
};

export const changeLanguage = language => {
    axios.defaults.headers['accept-language'] = language;
};

export const getUsers = (page = 0, size = 5) => {
    return axios.get(`/api/1.0/users?page=${page}&size=${size}`);
};

export const setAuthorizationHeader = ({isLoggedIn, token}) => {
    if(isLoggedIn) {
        const authHeaderValue = `Bearer ${token}`;
        axios.defaults.headers['Authorization'] = authHeaderValue;
    } else {
        delete axios.defaults.headers['Authorization'];
    }
};

export const getUser = username => {
    return axios.get(`/api/1.0/users/${username}`);
};

export const updateUser = (username, body) => {
    return axios.put(`/api/1.0/users/${username}`, body);
};

export const postContent = content => {
    return axios.post('/api/1.0/contents', content);
};

export const getContents = (username, page = 0) => {
    const path = username ? `/api/1.0/users/${username}/contents?page=` : '/api/1.0/contents?page=';
    return axios.get(path + page);
};

export const getOldContents = (id, username) => {
    const path = username ? `/api/1.0/users/${username}/contents/${id}` : `/api/1.0/contents/${id}`;
    return axios.get(path);
};

export const getNewContentCount = (id, username) => {
    const path = username ? `/api/1.0/users/${username}/contents/${id}?count=true` : `/api/1.0/contents/${id}?count=true`;
    return axios.get(path);
};

export const getNewContents = (id, username) => {
    const path = username ? `/api/1.0/users/${username}/contents/${id}?direction=after` : `/api/1.0/contents/${id}?direction=after`;
    return axios.get(path);
};

export const postContentAttachment = attachment => {
    return axios.post('/api/1.0/content-attachments', attachment);
};

export const deleteContent = id => {
    return axios.delete(`/api/1.0/contents/${id}`);
};

export const deleteUser = username => {
    return axios.delete(`/api/1.0/users/${username}`);
};