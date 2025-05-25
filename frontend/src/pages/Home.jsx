import React, { useState } from "react";
import "../styles/Home.css";
import {Link} from "react-router-dom";
function Home() {
    return(
        <div className="home-page">
            <div className="logo-container">
                <div className="logo">
                    <img src="src/assets/logo.svg"/>
                    <h1 className="banner">BiblioSolis</h1>
                </div>
            </div>
            <div className="home-actions">
                <Link to="/login" className="home-redirect">
                    Login
                </Link>
                <Link to="/register" className="home-redirect">
                    Register
                </Link>
            </div>
        </div>

    );

}

export default Home;