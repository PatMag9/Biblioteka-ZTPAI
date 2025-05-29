import "../styles/BookPages.css";
import logo from "../assets/logo_outline.svg";
import searchIcon from "../assets/search.svg";
import userIcon from "../assets/user.svg";
import React from "react";
import {Link, useNavigate} from "react-router-dom";
import axios from "axios";


function BookPageHeader({headerLink}) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await axios.post('http://localhost:8080/api/v1/auth/logout', {}, {
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
            <header className="book-header">
                <div className="header-logo">
                    {headerLink ? (
                        <Link to={headerLink}>
                            <img src={logo} alt="logo" />
                        </Link>
                    ) : (
                        <img src={logo} alt="logo" />
                    )}
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
    );
}

export default BookPageHeader;