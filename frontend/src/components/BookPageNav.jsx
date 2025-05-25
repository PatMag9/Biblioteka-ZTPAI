import React from "react";
import "../styles/BookPages.css";

function BookPageNav(){
    return (
        <div className="page-nav">
            <button className="prev-page">c</button>
            <div className="page-numbers">
                1
            </div>
            <button className="next-page">></button>
        </div>
    );
}

export default BookPageNav;