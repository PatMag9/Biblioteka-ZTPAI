import React, { useState, useEffect } from "react";
import {useParams, useNavigate} from "react-router-dom";
import "../styles/BookPages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";
import BookPageCategories from "../components/BookPageCategories.jsx";


const API_URL = "http://localhost:8080/api/v1/books";

function BookDetails() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [book, setBook] = useState(null);
    const [copies, setCopies] = useState([]);
    const [publishers, setPublishers] = useState({});
    const [authors, setAuthors] = useState([]);
    const [copyStatuses, setCopyStatuses] = useState({});
    const [token, setToken] = useState(null);

    useEffect(() => {
        const tokenFromStorage = localStorage.getItem("jwtToken");
        if (!tokenFromStorage) {
            alert("Brak tokenu uwierzytelniającego JWT.");
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

        if (isTokenExpired(tokenFromStorage)) {
            localStorage.removeItem("jwtToken");
            alert("Token uwierzytelniający JWT wygasł.");
            navigate("/");
            return;
        }

        setToken(tokenFromStorage);
    }, [navigate]);

    useEffect(() => {
        if (!token) return;
        fetch(`${API_URL}/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => res.json())
            .then((data) => setBook(data))
            .catch((err) => console.error("Błąd:", err));
    }, [id, token]);

    useEffect(() => {
        if (!token) return;
        fetch(`${API_URL}/${id}/copies`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                setCopies(data);
                data.forEach((copy) => {
                    fetch(`${API_URL}/copies/${copy.id_book_copy}/publisher`, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    })
                        .then((res) => {
                            if (!res.ok) throw new Error("Brak wydawcy");
                            return res.json();
                        })
                        .then((publisherData) => {
                            setPublishers((prev) => ({
                                ...prev,
                                [copy.id_book_copy]: publisherData.publishers_name,
                            }));
                        })
                        .catch((err) => {
                            console.warn(`Błąd wydawcy dla copy ${copy.id_book_copy}:`, err);
                            setPublishers((prev) => ({
                                ...prev,
                                [copy.id_book_copy]: "Nieznany",
                            }));
                        });
                });
            })
            .catch((err) => console.error("Błąd podczas pobierania egzemplarzy:", err));
    }, [id, token]);

    useEffect(() => {
        if (!token) return;
        fetch(`${API_URL}/${id}/authors`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (!res.ok) throw new Error("Błąd pobierania autorów");
                return res.json();
            })
            .then((data) => setAuthors(data))
            .catch((err) => console.error("Błąd podczas pobierania autorów:", err));
    }, [id, token]);

    useEffect(() => {
        if (!token || copies.length === 0) return;

        copies.forEach((copy) => {
            fetch(`http://localhost:8080/api/v1/checkout/bookCopyStatus/${copy.id_book_copy}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
                .then((res) => {
                    if (!res.ok) throw new Error("Błąd podczas sprawdzania statusu egzemplarza");
                    return res.json();
                })
                .then((data) => {
                    setCopyStatuses((prev) => ({
                        ...prev,
                        [copy.id_book_copy]: data,
                    }));
                })
                .catch((err) => {
                    console.error(`Błąd przy sprawdzaniu statusu egzemplarza ${copy.id_book_copy}:`, err);
                    setCopyStatuses((prev) => ({
                        ...prev,
                        [copy.id_book_copy]: {
                            reserved: false,
                            reservedByCurrentUser: false,
                            loaned: false,
                        },
                    }));
                });
        });
    }, [copies, token]);

    const handleReserve = (bookCopyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/reserve/${bookCopyId}`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                if (res.ok) {
                    alert("Rezerwacja została utworzona!");
                    refreshCopyStatus(bookCopyId);
                } else if (res.status === 404) {
                    alert("Nie znaleziono egzemplarza.");
                } else {
                    alert("Błąd podczas rezerwacji.");
                }
            })
            .catch(() => alert("Błąd podczas rezerwacji."));
    };

    const handleCancelReservation = (bookCopyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/cancelReservation/${bookCopyId}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    alert("Rezerwacja została anulowana.");
                    refreshCopyStatus(bookCopyId);
                } else {
                    alert("Błąd podczas anulowania rezerwacji.");
                }
            })
            .catch(() => alert("Błąd podczas anulowania rezerwacji."));
    };

    const refreshCopyStatus = (bookCopyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/bookCopyStatus/${bookCopyId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                setCopyStatuses((prev) => ({
                    ...prev,
                    [bookCopyId]: data,
                }));
            })
            .catch((err) => {
                console.error(`Błąd przy odświeżaniu statusu egzemplarza ${bookCopyId}:`, err);
            });
    };

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
                            <div className="book-cover">{book.cover && <img src={`/uploads/${book.cover}`} alt="cover_art"/>}</div>
                            <div className="book-details">
                                <p><strong>Tytuł: </strong>
                                    {book.title}
                                </p>
                                <p><strong>Autor/rzy:</strong>
                                    {authors.length > 0
                                        ? authors.map((author, index) => (
                                            <span key={author.id_author}>
                                            {author.name} {author.surname}{index < authors.length - 1 ? ', ' : ''}
                                        </span>
                                        ))
                                        : "brak danych"}
                                </p>
                                <p><strong>Gatunek:</strong> {book.genre.genre_name}</p>
                                <p><strong>Opis:</strong> {book.description}</p>
                            </div>
                        </div>
                        <section className="book-copies">
                            <h3>Egzemplarze książki</h3>
                            {copies.length > 0 ? (
                                <table className="copies-table">
                                    <thead>
                                    <tr>
                                        <th>ID Egzemplarza</th>
                                        <th>IBSN</th>
                                        <th>Wydawca</th>
                                        <th>Rok Publikacji</th>
                                        <th>Akcja</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {copies.map((copy) => {
                                        const status = copyStatuses[copy.id_book_copy];
                                        const isLoaned = status?.loaned;
                                        const isReserved = status?.reserved;
                                        const reservedByCurrentUser = status?.reservedByCurrentUser;
                                        let actionButton = null;
                                        if (isLoaned) {
                                            actionButton = null;
                                        } else if (isReserved) {
                                            if (reservedByCurrentUser) {
                                                actionButton = (
                                                    <button onClick={() => handleCancelReservation(copy.id_book_copy)}>
                                                        Anuluj rezerwację
                                                    </button>
                                                );
                                            } else {
                                                actionButton = null;
                                            }
                                        } else {
                                            actionButton = (
                                                <button onClick={() => handleReserve(copy.id_book_copy)}>Zarezerwuj</button>
                                            );
                                        }

                                        return (
                                            <tr key={copy.id_book_copy}>
                                                <td>{copy.id_book_copy}</td>
                                                <td>{copy.isbn}</td>
                                                <td>{publishers[copy.id_book_copy] || "Ładowanie..."}</td>
                                                <td>{copy.year_published}</td>
                                                <td>{actionButton}</td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </table>
                            ) : (
                                <p>Brak dostępnych egzemplarzy.</p>
                            )}
                        </section>
                    </div>
                </section>
            </main>
        </div>
    );
}

export default BookDetails;
