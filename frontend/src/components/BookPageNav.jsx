import React from "react";
import "../styles/BookPages.css";

function BookPageNav({ currentPage, totalPages, onPageChange }) {
    const handlePrev = () => {
        if (currentPage > 0) {
            onPageChange(currentPage - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            onPageChange(currentPage + 1);
        }
    };

    return (
        <div className="page-nav">
            <button
                className="prev-page"
                onClick={handlePrev}
                disabled={currentPage === 0}
            >
                {"<"}
            </button>

            <div className="page-numbers">
                {currentPage + 1} / {totalPages}
            </div>

            <button
                className="next-page"
                onClick={handleNext}
                disabled={currentPage === totalPages - 1}
            >
                {">"}
            </button>
        </div>
    );
}

export default BookPageNav;