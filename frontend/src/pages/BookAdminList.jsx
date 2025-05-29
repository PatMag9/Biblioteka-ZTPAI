import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import "../styles/AdminPages.css"
import { jwtDecode } from "jwt-decode";

function BookAdminList() {
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
    const [authorsMap, setAuthorsMap] = useState({});

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
            return;
        }

        try {
            const decoded = jwtDecode(token);
            const roles = decoded.roles || [];

            if (!roles.includes("ROLE_ADMIN")) {
                alert("Nie masz dostępu do tej strony.");
                navigate("/books");
            }
        } catch (err) {
            console.error("Błąd dekodowania tokena:", err);
            navigate("/");
        }
    }, [navigate]);

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
        fetch("http://localhost:8080/api/v1/books", {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((response) => response.json())
            .then((data) => {
                setBooks(data);
                fetchAuthorsForBooks(data);
                setIsLoading(false);
            })
            .catch((error) => console.error("Błąd:", error));
    };

    const fetchAuthorsForBooks = async (books) => {
        const token = localStorage.getItem("jwtToken");

        const authorsData = await Promise.all(
            books.map((book) =>
                fetch(`http://localhost:8080/api/v1/books/${book.idBook}/authors`, {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                    },
                })
                    .then((res) => res.ok ? res.json() : [])
                    .catch(() => [])
            )
        );

        const authorsMap = {};
        books.forEach((book, index) => {
            authorsMap[book.idBook] = authorsData[index];
        });
        setAuthorsMap(authorsMap);
    };

    const fetchGenres = () => {
        fetch("http://localhost:8080/api/v1/genres", {
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
        fetch("http://localhost:8080/api/v1/books", {
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
        <div className="admin-container">
            <h1>Książki</h1>
            <div className="admin-book-list">
                <div className="admin-book-list-header">
                    <div className="admin-header-element">Okładka</div>
                    <div className="admin-header-element">Tytuł</div>
                    <div className="admin-header-element">Autor</div>
                    <div className="admin-header-element">Gatunek</div>
                    <div className="admin-header-element">Akcja</div>
                </div>
                <div className="admin-book-list-body">
                    {isLoading ? (
                        <p>Ładowanie książek...</p>
                    ) : (
                        books.map(book => (
                            <div className="admin-book-card" key={book.idBook}>
                                <div className="admin-book-card-element book-cover">
                                    <Link to={`/book/${book.idBook}`}>
                                        {book.cover && <img src={`/uploads/${book.cover}`} alt="cover_art"/>}
                                    </Link>
                                </div>
                                <div className="admin-book-card-element">
                                    {book.title}
                                </div>
                                <div className="admin-book-card-element">
                                    {authorsMap[book.idBook] && authorsMap[book.idBook].length > 0
                                        ? authorsMap[book.idBook].map((author, index) => (
                                            <span key={author.id_author}>
                                                    {author.name} {author.surname}{index < authorsMap[book.idBook].length - 1 ? ', ' : ''}
                                                </span>
                                        ))
                                        : "brak danych"}
                                </div>
                                <div className="admin-book-card-element">
                                    {book.genre.genre_name}
                                </div>
                                <div className="admin-book-card-element">
                                    <Link to={`/book/${book.idBook}`}>
                                        <button className="reserve-button">Zobacz</button>
                                    </Link>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}

export default BookAdminList;
