import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../styles/BookPages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";
import BookPageCategories from "../components/BookPageCategories.jsx";

const API_URL = "http://localhost:8080/api/v1/books";

function BookDetails() {
    const navigate = useNavigate();
    const { id } = useParams();

    const [book, setBook] = useState(null);
    const [copyStatuses, setCopyStatuses] = useState({});
    const [token, setToken] = useState(null);

    // ───────────────────────────────────────────────────────────
    // 1. WALIDACJA TOKENU
    // ───────────────────────────────────────────────────────────
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

    // ───────────────────────────────────────────────────────────
    // 2. POBIERANIE DANYCH KSIĄŻKI (DTO)
    // ───────────────────────────────────────────────────────────
    useEffect(() => {
        if (!token) return;

        fetch(`${API_URL}/${id}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => setBook(data))
            .catch(err => console.error("Błąd:", err));
    }, [id, token]);

    // ───────────────────────────────────────────────────────────
    // 3. STATUSY EGZEMPLARZY
    // ───────────────────────────────────────────────────────────
    useEffect(() => {
        if (!token || !book?.copies) return;

        book.copies.forEach(copy => {
            fetch(`http://localhost:8080/api/v1/checkout/bookCopyStatus/${copy.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then(res => res.json())
                .then(status => {
                    setCopyStatuses(prev => ({
                        ...prev,
                        [copy.id]: status
                    }));
                })
                .catch(() => {
                    setCopyStatuses(prev => ({
                        ...prev,
                        [copy.id]: {
                            reserved: false,
                            reservedByCurrentUser: false,
                            loaned: false,
                        }
                    }));
                });
        });
    }, [book, token]);

    // ───────────────────────────────────────────────────────────
    // 4. AKCJE REZERWACJI
    // ───────────────────────────────────────────────────────────
    const refreshCopyStatus = (copyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/bookCopyStatus/${copyId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => {
                setCopyStatuses(prev => ({
                    ...prev,
                    [copyId]: data
                }));
            });
    };

    const handleReserve = (copyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/reserve/${copyId}`, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) return alert("Błąd podczas rezerwacji.");
                alert("Rezerwacja została utworzona!");
                refreshCopyStatus(copyId);
            });
    };

    const handleCancelReservation = (copyId) => {
        fetch(`http://localhost:8080/api/v1/checkout/cancelReservation/${copyId}`, {
            method: "PUT",
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) return alert("Błąd podczas anulowania rezerwacji.");
                alert("Rezerwacja została anulowana.");
                refreshCopyStatus(copyId);
            });
    };

    // ───────────────────────────────────────────────────────────
    // 5. RENDER
    // ───────────────────────────────────────────────────────────
    if (!book) return <p>Ładowanie...</p>;

    return (
        <div className="container">
            <BookPageHeader />
            <main>
                <h2>Katalog Biblioteki BiblioSolis</h2>
                <BookPageCategories />

                <section className="book-list">
                    <div className="book-page">
                        <div className="book-info">
                            <div className="book-cover">
                                {book.cover && <img src={`/uploads/${book.cover}`} alt="cover_art" />}
                            </div>

                            <div className="book-details">
                                <p><strong>Tytuł: </strong> {book.title}</p>

                                <p><strong>Autor/rzy: </strong>
                                    {book.authors?.map((a, index) => (
                                        <span key={a.id}>
                                            {a.name} {a.surname}{index < book.authors.length - 1 ? ", " : ""}
                                        </span>
                                    ))}
                                </p>

                                <p><strong>Gatunek: </strong> {book.genre?.name}</p>
                                <p><strong>Opis: </strong> {book.description}</p>
                            </div>
                        </div>

                        {/* EGZEMPLARZE */}
                        <section className="book-copies">
                            <h3>Egzemplarze książki</h3>

                            {book.copies?.length > 0 ? (
                                <table className="copies-table">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>ISBN</th>
                                        <th>Wydawca</th>
                                        <th>Rok</th>
                                        <th>Akcja</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {book.copies.map(copy => {
                                        const status = copyStatuses[copy.id] || {};

                                        const isLoaned = status.loaned;
                                        const isReserved = status.reserved;
                                        const reservedByUser = status.reservedByCurrentUser;

                                        let button = null;

                                        if (isLoaned) button = null;
                                        else if (isReserved && reservedByUser)
                                            button = (
                                                <button onClick={() => handleCancelReservation(copy.id)}>
                                                    Anuluj rezerwację
                                                </button>
                                            );
                                        else if (!isReserved)
                                            button = (
                                                <button onClick={() => handleReserve(copy.id)}>
                                                    Zarezerwuj
                                                </button>
                                            );

                                        return (
                                            <tr key={copy.id}>
                                                <td>{copy.id}</td>
                                                <td>{copy.isbn}</td>
                                                <td>{copy.publisher?.name ?? "Nieznany"}</td>
                                                <td>{copy.yearPublished}</td>
                                                <td>{button}</td>
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
