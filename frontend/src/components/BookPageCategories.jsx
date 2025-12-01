import React from "react";
import "../styles/BookPages.css";

function BookPageCategories({ onSortChange }) {
    return (
        <nav>
            <ul>
                <div className="nav-element books-new">
                    <div className="nav-text">Nowości</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-popular">
                    <div className="nav-text">Popularne</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-alphabetical"
                     onClick={() => onSortChange("title")}>
                    <div className="nav-text">Alfabetycznie</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-publisher"
                     onClick={() => onSortChange("author")}>
                    <div className="nav-text">Autorzy</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-genres"
                     onClick={() => onSortChange("genre")}>
                    <div className="nav-text">Gatunki</div>
                    <div className="nav-line"></div>
                </div>
            </ul>
        </nav>
    );
}

export default BookPageCategories;