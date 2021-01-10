import React from 'react';
import { useTranslation } from 'react-i18next';
import ButtonWithProgress from './ButtonWithProgress';

const Modal = ({ visible, onClickCancel, message, onClickOk, pendingApiCall, title, buttonText, buttonId }) => {

    let className = 'modal fade'
    if(visible) {
        className += ' show d-block'
    }

    const { t } = useTranslation();
    return (
        <div className={className} style={{ backgroundColor: '#000000b0' }}>
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">{title}</h5>
                    </div>
                    <div className="modal-body">
                        <div>{message}</div>
                    </div>
                    <div className="modal-footer">
                        <ButtonWithProgress 
                        className="btn btn-danger" 
                        onClick={onClickOk}
                        text={buttonText}
                        pendingApiCall={pendingApiCall}
                        disabled={pendingApiCall}
                        id={buttonId}
                        >
                        </ButtonWithProgress>
                        <button type="button" className="btn btn-secondary" onClick={onClickCancel} disabled={pendingApiCall}>{t('Cancel')}</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Modal;