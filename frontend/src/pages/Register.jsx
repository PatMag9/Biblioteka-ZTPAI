
import "../styles/HomePages.css"
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";


function Register() {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async(e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/register', {
                username,
                email,
                password
            });
            const token = response.data.token;
            localStorage.setItem('jwtToken', token);
            navigate('/books');
        } catch (err) {
            setError('Invalid data');
        }
    }

    return(
      <div className="home-page">
          <div className="logo-container">
              <div className="logo">
                  <img src="src/assets/logo.svg"/>
                  <h1 className="banner">BiblioSolis</h1>
              </div>
          </div>
          <form className="home-actions" onSubmit={handleRegister}>
              <input
                  className="input-button"
                  type="text"
                  placeholder="username"
                  value={username}
                  onChange={(e)=>setUsername(e.target.value)}
              />
              <input
                  className="input-button"
                  type="text"
                  placeholder="email"
                  value={email}
                  onChange={(e)=>setEmail(e.target.value)}
              />
              <input
                  className="input-button"
                  type="password"
                  placeholder="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
              />
              <button className="home-redirect" type="submit">Register</button>
              {error && <p className="error">{error}</p>}
          </form>
      </div>
    );
}

export default Register;