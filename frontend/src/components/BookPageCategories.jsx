import React from "react";
import "../styles/BookPages.css";
import {Link} from "react-router-dom";

function BookPageCategories({ onSortChange }) {
    return (
        <nav>
            <ul>
                <div className="nav-element books-new" onClick={() => onSortChange("newest")}>
                    <div className="nav-text">Nowości</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-popular" onClick={() => onSortChange("popular")}>
                    <div className="nav-text">Popularne</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-alphabetical" onClick={() => onSortChange("alphabetical")}>
                    <div className="nav-text">Alfabetycznie</div>
                    <div className="nav-line"></div>
                </div>

                <div className="nav-element books-authors">
                    <Link to="/authors">
                        <div className="nav-text">Autorzy</div>
                        <div className="nav-line"></div>
                    </Link>
                </div>

                <div className="nav-element books-genres">
                    <Link to="/genres">
                        <div className="nav-text">Gatunki</div>
                        <div className="nav-line"></div>
                    </Link>
                </div>
            </ul>
        </nav>
    );
}

export default BookPageCategories;