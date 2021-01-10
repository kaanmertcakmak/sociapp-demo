import React from 'react';

const ButtonWithProgress = (props) => {

    const { onClick, pendingApiCall, disabled, text, className, id } = props;

    return (
        <button
        className={className || "btn btn-info"}
        onClick={onClick}
        id={id}
        disabled={disabled}
        >
            {pendingApiCall && <span className="spinner-border spinner-border-sm"></span>}
            {text}
       </button> 
    );
};

export default ButtonWithProgress;