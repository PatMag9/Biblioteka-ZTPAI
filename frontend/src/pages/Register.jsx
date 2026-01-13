import "../styles/HomePages.css"
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";


function Register() {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const validateForm = () => {
        const newErrors = {};

        if (!username.trim()) {
            newErrors.username = "Nazwa użytkownika jest wymagana";
        } else if (username.length < 3) {
            newErrors.username = "Nazwa musi zawierać conajmniej 3 znaki";
        }

        if (!email.trim()) {
            newErrors.email = "Adres email jest wymagany";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            newErrors.email = "Niepoprawny adres email";
        }

        if (!password) {
            newErrors.password = "Hasło jest wymagane";
        } else if ((password.length < 8) || (!/[a-z]/.test(password)) || (!/[A-Z]/.test(password)) ||
            (!/[0-9]/.test(password)) || (!/[!@#$%^&*(),.?":{}|<>]/.test(password))) {
            newErrors.password = "Hasło musi zawierać conajmniej 8 znaków, w tym małą literę, dużą litere, cyfrę i znak specjalny";
        }

        if (password.toLowerCase().includes(username.toLowerCase())) {
            newErrors.password = "Hasła muszą się zgadzać";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleRegister = async(e) => {
        e.preventDefault();
        if (!validateForm()) return;
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
            setErrors({ server: "Registration failed. Try again." });
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
                  onChange={(e) => setUsername(e.target.value)}
              />
              {errors.username && <p className="error">{errors.username}</p>}
              <input
                  className="input-button"
                  type="text"
                  placeholder="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
              />
              {errors.email && <p className="error">{errors.email}</p>}
              <input
                  className="input-button"
                  type="password"
                  placeholder="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
              />
              <input
                  className="input-button"
                  type="confirmPassword"
                  placeholder="confirm password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
              />
              {errors.password && <p className="error">{errors.password}</p>}
              <button className="home-redirect" type="submit">Register</button>
              {errors.server && <p className="error">{errors.server}</p>}
          </form>
      </div>
    );
}

export default Register;