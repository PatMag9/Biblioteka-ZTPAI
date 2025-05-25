import React, { useState, useEffect } from "react";
import {Link, useNavigate} from "react-router-dom";
import "../styles/BookPages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";
import BookPageCategories from "../components/BookPageCategories.jsx";
import BookPageNav from "../components/BookPageNav.jsx";

const API_URL = "http://localhost:8080/api/books";
const GENRE_API_URL = "http://localhost:8080/api/genres";

function BooksList() {
    const navigate = useNavigate();
    const [books, setBooks] = useState([]);
    const [newBook, setNewBook] = useState({
        title: "",
        genre: {},
        cover: "",
        description: "",
    });
    const [genres, setGenres] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

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

        fetchBooks();
        fetchGenres();
    }, [navigate]);

    const fetchBooks = () => {
        fetch(API_URL, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((response) => response.json())
            .then((data) => {
                setBooks(data);
                setIsLoading(false);
            })
            .catch((error) => console.error("Błąd:", error));
    };

    const fetchGenres = () => {
        fetch(GENRE_API_URL, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((response) => response.json())
            .then((data) => setGenres(data))
            .catch((error) => console.error("Błąd:", error));
    };

    useEffect(() => {
        fetchBooks();
        fetchGenres();
    }, []);

    const handleChange = (e) => {
        if (e.target.name === "genre") {
            const selectedGenre = genres.find((genre) => genre.id_genre === parseInt(e.target.value));
            setNewBook({ ...newBook, genre: selectedGenre });
        } else {
            setNewBook({ ...newBook, [e.target.name]: e.target.value });
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: JSON.stringify(newBook),
        })
            .then((res) => res.json())
            .then((data) => {
                setBooks([...books, data]);
                setNewBook({ title: "", genre: {}, cover: "", description: "" });
            })
            .catch((err) => console.error("Błąd:", err));
    };

    return (
        <div className="container">
            <BookPageHeader/>
            <main>
                <h2>Katalog Biblioteki BiblioSolis</h2>
                <BookPageCategories/>
                <BookPageNav/>
                <section className="book-list">
                    {isLoading ? (
                        <p>Ładowanie książek...</p>
                    ) : (
                        books.map(book => (
                            <div className="book-card">
                                <div className="book-cover">
                                    <Link to={`/book/${book.id_book}`}>
                                        {book.cover && <img src={book.cover} alt="cover_art" />}
                                    </Link>
                                </div>
                                <div className="book-details">
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
                                <button className="reserve-button">Zarezerwuj</button>
                            </div>
                        ))
                    )}
                </section>
            </main>
        </div>
);
}

export default BooksList;
