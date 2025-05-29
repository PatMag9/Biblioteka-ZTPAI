import "../styles/BookPages.css";
import logo from "../assets/logo_outline.svg";
import searchIcon from "../assets/search.svg";
import userIcon from "../assets/user.svg";
import React from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";


function BookPageHeader() {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await axios.post('http://localhost:8080/api/auth/logout', {}, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
                }
            });
        } catch (error) {
            console.error("Logout error (ignored):", error);
        }
        localStorage.removeItem('jwtToken');
        navigate('/');
    };
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
                        <button id="user-button" onClick={handleLogout}>
                            <img src={userIcon} alt="Użytkownik"/>
                        </button>
                    </div>
                </div>
            </header>
        </div>
    );
}

export default BookPageHeader;