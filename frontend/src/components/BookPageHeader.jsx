import "../styles/BookPages.css";
import logo from "../assets/logo_outline.svg";
import searchIcon from "../assets/search.svg";
import userIcon from "../assets/user.svg";
import React from "react";


function BookPageHeader() {
    return (
        <div className="container">
            <header className="book-header">
                <div className="header-logo">
                    <img src={logo}/>
                    <div className="banner">BiblioSolis</div>
                </div>
                <div className="header-right-side">
                    <input name="search-input" placeholder="Wyszukaj..."/>
                    <button className="search-button"><img src={searchIcon}/></button>
                    <div className="user-component">
                        <button id="user-button"><img src={userIcon}/></button>
                    </div>
                </div>
            </header>
        </div>
    );
}

export default BookPageHeader;