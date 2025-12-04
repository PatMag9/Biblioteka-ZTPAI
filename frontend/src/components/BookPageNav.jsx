import React from "react";
import "../styles/BookPages.css";

function BookPageNav({ currentPage, totalPages, onPageChange }) {

    const MAX_VISIBLE = 5;

    const getPageNumbers = () => {
        let pages = [];

        if (totalPages <= MAX_VISIBLE + 2) {
            for (let i = 0; i < totalPages; i++) {
                pages.push(i);
            }
            return pages;
        }

        if (currentPage < MAX_VISIBLE) {
            for (let i = 0; i < MAX_VISIBLE; i++) {
                pages.push(i);
            }
            pages.push("ellipsis");
            pages.push(totalPages - 1);
            return pages;
        }

        if (currentPage >= totalPages - MAX_VISIBLE) {
            pages.push(0);
            pages.push("ellipsis");
            for (let i = totalPages - MAX_VISIBLE; i < totalPages; i++) {
                pages.push(i);
            }
            return pages;
        }

        pages.push(0);
        pages.push("ellipsis");

        const start = currentPage - 1;
        const end = currentPage + 1;

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }

        pages.push("ellipsis");
        pages.push(totalPages - 1);

        return pages;
    };

    const handlePrev = () => {
        if (currentPage > 0) onPageChange(currentPage - 1);
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) onPageChange(currentPage + 1);
    };

    return (
        <div className="page-nav">
            <button onClick={handlePrev} disabled={currentPage === 0} className="prev-page">
                {"<"}
            </button>

            <div className="page-numbers">
                {getPageNumbers().map((num, index) => {
                    if (num === "ellipsis") {
                        return <span key={index} className="ellipsis">...</span>;
                    }
                    return (
                        <button
                            key={index}
                            className={`page-number ${num === currentPage ? "active" : ""}`}
                            onClick={() => onPageChange(num)}
                        >
                            {num + 1}
                        </button>
                    );
                })}
            </div>

            <button onClick={handleNext} disabled={currentPage === totalPages - 1} className="next-page">
                {">"}
            </button>
        </div>
    );
}

export default BookPageNav;
