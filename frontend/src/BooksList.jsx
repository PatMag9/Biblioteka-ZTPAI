import React, { useState, useEffect } from "react";
import {Link, useNavigate} from "react-router-dom";
import "./CSS/styles.css";

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
            <h1>Lista Książek</h1>

            <button className="refresh-btn" onClick={fetchBooks}>
                Odśwież
            </button>

            {isLoading ? (
                <p>Ładowanie książek...</p>
            ) : (
                <ul className="book-list">
                    {books.map((book) => (
                        <li key={book.id_book} className="book-item">
                            <Link to={`/book/${book.id_book}`} className="details-link">
                                {book.title}
                            </Link>
                        </li>
                    ))}
                </ul>
            )}

            <div className="book-form">
                <h2>Dodaj książkę</h2>
                <form onSubmit={handleSubmit}>
                        <input type="text" name="title" placeholder="Tytuł" value={newBook.title} onChange={handleChange} required/>
                        <input type="text" name="cover" placeholder="Okładka" value={newBook.cover} onChange={handleChange} required/>
                        <textarea name="description" placeholder="Opis" value={newBook.description} onChange={handleChange} required/>
                        <select name="genre" value={newBook.genre.id_genre || ""} onChange={handleChange} required>
                            <option value="">Wybierz gatunek</option>
                            {genres.map((genre) => (
                                <option key={genre.id_genre} value={genre.id_genre}>
                                    {genre.genre_name}
                                </option>
                            ))}
                        </select>
                    <button type="submit">Dodaj książkę</button>
                </form>
            </div>
        </div>
    );
}

export default BooksList;
