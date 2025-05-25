import React, { useState, useEffect } from "react";
import {useParams, Link, useNavigate} from "react-router-dom";
import "../styles/BookPages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";
import BookPageCategories from "../components/BookPageCategories.jsx";


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
            <BookPageHeader/>
            <main>
                <h2>Katalog Biblioteki BiblioSolis</h2>
                <BookPageCategories/>
                <section className="book-list">
                    <div className="book-page">
                        <div className="book-info">
                            <div className="book-cover">{book.cover && <img src={book.cover} alt="cover_art" />}</div>
                            <div class="book-details">
                                <p><strong>Tytuł: </strong>
                                    <Link to={`/book/${book.id_book}`}>{book.title}</Link>
                                </p>
                                <p><strong>Autor/rzy:</strong>
                                    {/* todo */}
                                </p>
                                <p><strong>Gatunek:</strong> {book.genre.genre_name}</p>
                                <p><strong>Wydawnictwo:</strong> {/* todo */}</p>
                                <p><strong>Status:</strong> {/* todo */}</p>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    );
}

export default BookDetails;
