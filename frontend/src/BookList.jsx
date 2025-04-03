import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./styles.css";

const API_URL = "http://localhost:8080/api/books";

function BooksList() {
    const [books, setBooks] = useState([]);
    const [newBook, setNewBook] = useState({
        title: "",
        id_genre: "",
        cover: "",
        description: "",
    });

    // Funkcja pobierająca książki
    const fetchBooks = () => {
        fetch("http://localhost:8080/api/books")
            .then((response) => response.json())
            .then((data) => setBooks(data))
            .catch((error) => console.error("Błąd:", error));
    };

    // Pobierz książki przy załadowaniu komponentu
    useEffect(() => {
        fetchBooks();
    }, []);

    // Obsługa formularza dodawania książki
    const handleChange = (e) => {
        setNewBook({ ...newBook, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newBook),
        })
            .then((res) => res.json())
            .then((data) => setBooks([...books, data])) // Aktualizacja listy
            .catch((err) => console.error("Błąd:", err));

        setNewBook({ title: "", id_genre: "", cover: "", description: "" });
    };

    return (
        <div className="container">
            <h1>Lista Książek</h1>
            <button className="refresh-btn" onClick={fetchBooks}>
                Odśwież
            </button>
            <ul className="book-list">
                {books.map((book) => (
                    <li key={book.id_book} className="book-item">
                        <Link to={`/book/${book.id_book}`} className="details-link">{book.title}</Link>
                    </li>
                ))}
            </ul>

            <h2>Dodaj nową książkę</h2>
            <form onSubmit={handleSubmit}>
                <input type="text" name="title" placeholder="Tytuł" value={newBook.title} onChange={handleChange}
                       required/>
                <input type="number" name="id_genre" placeholder="ID Gatunku" value={newBook.id_genre}
                       onChange={handleChange} required/>
                <input type="text" name="cover" placeholder="Okładka" value={newBook.cover}
                       onChange={handleChange}/>
                <textarea name="description" placeholder="Opis" value={newBook.description} onChange={handleChange}
                          required/>
                <button type="submit">➕ Dodaj książkę</button>
            </form>
        </div>
    );
}

export default BooksList;
