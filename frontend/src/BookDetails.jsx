import React, { useState, useEffect } from "react";
import {useParams, Link, useNavigate} from "react-router-dom";
import "./CSS/styles.css";

const API_URL = "http://localhost:8080/api/books";

function BookDetails() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [book, setBook] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            navigate("/");
            return;
        }

        const isTokenExpired = (token) => {
            try {
                const payload = JSON.parse(atob(token.split(".")[1]));
                return payload.exp * 1000 < Date.now();
            } catch {
                return true;
            }
        };

        if (isTokenExpired(token)) {
            localStorage.removeItem("jwtToken");
            navigate("/");
        }

    }, [navigate]);

    useEffect(() => {
        fetch(`${API_URL}/${id}`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((res) => res.json())
            .then((data) => setBook(data))
            .catch((err) => console.error("Błąd:", err));
    }, [id]);

    if (!book) return <p>Ładowanie...</p>;

    return (
        <div className="container">
            <div className="book-details">
                <h1>{book.title}</h1>
                <p><strong>Gatunek:</strong> {book.genre.genre_name}</p>
                <p><strong>Opis:</strong> {book.description}</p>
                {book.cover && <img src={book.cover} alt="cover_art" width="200" />}
                <br />
                <Link to="/books" className="back-btn">Powrót do listy</Link>
            </div>
        </div>
    );
}

export default BookDetails;
