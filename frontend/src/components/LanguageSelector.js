import React from 'react';
import { useTranslation } from 'react-i18next';
import { changeLanguage } from '../api/apiCalls';

const LanguageSelector = () => {

    const { i18n } = useTranslation();

    const onChangeLanguage = language => {
        i18n.changeLanguage(language);
        changeLanguage(language);
    };

    return (
        <div className='container'>
            <img 
                src="https://www.countryflags.io/tr/flat/24.png"
                alt="turkish flag" 
                onClick={() => onChangeLanguage('tr')}
                style={{cursor: 'pointer'}}>
            </img>
            <img 
                src="https://www.countryflags.io/us/flat/24.png"
                alt="us flag" 
                onClick={() => onChangeLanguage('en')}
                style={{cursor: 'pointer'}}>
            </img>
    </div>   
    );
};

export default LanguageSelector;