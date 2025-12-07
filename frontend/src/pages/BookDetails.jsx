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
    const [reviews, setReviews] = useState([]);
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

    // Pobranie danych książki
    useEffect(() => {
        if (!token) return;

        fetch(`${API_URL}/${id}`, { headers: { Authorization: `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => setBook(data))
            .catch(err => console.error("Błąd:", err));
    }, [id, token]);

    // Pobranie recenzji książki
    useEffect(() => {
        if (!token) return;

        fetch(`${API_URL}/${id}/reviews`, { headers: { Authorization: `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => setReviews(data))
            .catch(err => console.error("Błąd pobierania recenzji:", err));
    }, [id, token]);

    const renderStars = (averageRating) => {
        const fullStars = Math.floor(averageRating || 0);
        const emptyStars = 5 - fullStars;
        return (
            <>
                {Array.from({ length: fullStars }, (_, i) => (
                    <span key={`full-${i}`} className="star full">★</span>
                ))}
                {Array.from({ length: emptyStars }, (_, i) => (
                    <span key={`empty-${i}`} className="star empty">★</span>
                ))}
            </>
        );
    };
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
                                <p><strong>Tytuł: </strong>{book.title}</p>

                                <p><strong>Autor/rzy: </strong>
                                    {book.authors?.map((a, index) => (
                                        <span key={a.id}>
                                            {a.name} {a.surname}{index < book.authors.length - 1 ? ", " : ""}
                                        </span>
                                    ))}
                                </p>

                                <p><strong>Gatunek: </strong>{book.genre?.name}</p>
                                <p><strong>Opis: </strong>{book.description}</p>

                                <p className="book-rating">
                                    {renderStars(book.averageRating)}
                                    <span className="review-count">
                                        ({book.reviewCount || 0})
                                    </span>
                                </p>
                            </div>
                        </div>

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

                    <div className="book-page reviews-section">
                        <h3>Recenzje</h3>
                        {reviews.length === 0 ? (
                            <p>Brak recenzji dla tej książki.</p>
                        ) : (
                            <ul className="reviews-list">
                                {reviews.map(review => (
                                    <li key={review.id} className="review-item">
                                        <div className="review-header">
                                            {renderStars(review.rating)}
                                            <span className="review-username">{review.username}</span>
                                            <span className="review-date">{new Date(review.createdAt).toLocaleDateString()}</span>
                                        </div>
                                        <div className="review-text">{review.reviewText}</div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </section>
            </main>
        </div>
    );
}

export default BookDetails;
