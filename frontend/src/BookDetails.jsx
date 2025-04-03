import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import "./styles.css";

const API_URL = "http://localhost:8080/api/books";

function BookDetails() {
    const { id } = useParams();
    const [book, setBook] = useState(null);

    useEffect(() => {
        fetch(`${API_URL}/${id}`)
            .then((res) => res.json())
            .then((data) => setBook(data))
            .catch((err) => console.error("Błąd:", err));
    }, [id]);

    if (!book) return <p>Ładowanie...</p>;

    return (
        <div className="container">
            <div className="book-details">
                <h1>{book.title}</h1>
                <p><strong>Gatunek:</strong> {book.id_genre}</p>
                <p><strong>Opis:</strong> {book.description}</p>
                {book.cover && <img src={book.cover} alt="cover_art" width="200" />}
                <br />
                <Link to="/" className="back-btn">Powrót do listy</Link>
            </div>
        </div>
    );
}

export default BookDetails;
