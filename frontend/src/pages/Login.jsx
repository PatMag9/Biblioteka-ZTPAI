import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from "jwt-decode";
import axios from 'axios';
import '../styles/HomePages.css';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
                email,
                password
            });
            const token = response.data.token;
            localStorage.setItem('jwtToken', token);

            const decoded = jwtDecode(token);
            const roles = decoded.roles || [];

            if (roles.includes("ROLE_ADMIN")) {
                navigate('/admin');
            } else {
                navigate('/books');
            }
        } catch (err) {
            console.error('Login error:', err);
            setError('Invalid email or password');
        }
    };

    return (
        <div className="home-page">
            <div className="logo-container">
                <div className="logo">
                    <img src="src/assets/logo.svg"/>
                    <h1 className="banner">BiblioSolis</h1>
                </div>
            </div>
            <form className="home-actions" onSubmit={handleLogin}>
                <input
                    className="input-button"
                    type="text"
                    placeholder="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    className="input-button"
                    type="password"
                    placeholder="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button className="home-redirect" type="submit">Login</button>
                {error && <p className="error">{error}</p>}
            </form>
        </div>
    );
};

export default Login;
