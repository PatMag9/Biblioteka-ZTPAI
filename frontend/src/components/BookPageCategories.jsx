import React from "react";
import "../styles/BookPages.css";

function BookPageCategories() {
    return (
        <nav>
            <ul>
                <div className="nav-element books-new">
                    <div className="nav-text">
                        Nowości
                    </div>
                    <div className="nav-line"></div>
                </div>
                <div className="nav-element books-popular">
                    <div className="nav-text">
                        Popularne
                    </div>
                    <div className="nav-line"></div>
                </div>
                <div className="nav-element books-alphabetical">
                    <div className="nav-text">
                        Alfabetycznie
                    </div>
                    <div className="nav-line"></div>
                </div>
                <div className="nav-element books-publisher">
                    <div className="nav-text">
                        Wydawnictwo
                    </div>
                    <div className="nav-line"></div>
                </div>
                <div className="nav-element books-genres">
                    <div className="nav-text">
                        Gatunki
                    </div>
                    <div className="nav-line"></div>
                </div>
            </ul>
        </nav>
    );
}

export default BookPageCategories;