import React from 'react';

const Spinner = () => {
    return (
        <div className="d-flex justify-content-center">
            <div className="spinner-grow text-info">
                <span className="sr-only">Loading...</span>
            </div>
        </div>
    );
};

export default Spinner;