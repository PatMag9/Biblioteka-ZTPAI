import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import "../styles/AdminPages.css"
import { jwtDecode } from "jwt-decode";
import BookPageHeader from "../components/BookPageHeader.jsx";



function ReservationsList() {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [reservations, setReservations] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
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

        if (isTokenExpired(token)) {
            localStorage.removeItem("jwtToken");
            alert("Token uwierzytelniający JWT wygasł.");
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

    const fetchReservations = () => {
        setIsLoading(true);
        fetch("http://localhost:8080/api/v1/checkout/reservations", {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Błąd podczas pobierania rezerwacji");
                }
                return res.json();
            })
            .then((data) => {
                setReservations(data);
                setIsLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setIsLoading(false);
            });
    };

    useEffect(() => {
        fetchReservations();
    }, []);

    const handleCompleteReservation = (reservationId) => {
        fetch(`http://localhost:8080/api/v1/checkout/reservations/${reservationId}/confirm`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((res) => {
                if (!res.ok) {
                    return res.text().then((text) => {
                        throw new Error(text || "Nie udało się zakończyć rezerwacji");
                    });
                }
                return res.text();
            })
            .then((message) => {
                alert(message);
                fetchReservations();
            })
            .catch((err) => {
                alert("Błąd: " + err.message);
            });
    };

    if (isLoading) {
        return <div className="admin-container">Ładowanie rezerwacji...</div>;
    }

    if (error) {
        return <div className="admin-container">Wystąpił błąd: {error}</div>;
    }

    return (
        <>
            <BookPageHeader headerLink="/admin" />
            <div className="admin-container">
                <h1>Rezerwacje</h1>
                <div className="admin-book-list">
                    <div className="admin-book-list-header">
                        <div className="admin-header-element">Id wypożyczenia</div>
                        <div className="admin-header-element">Użytkownik</div>
                        <div className="admin-header-element">Książka</div>
                        <div className="admin-header-element">Data rozpoczęcia</div>
                        <div className="admin-header-element">Akcja</div>
                    </div>
                    <div className="admin-book-list-body">
                        {reservations.length === 0 && (
                            <div className="admin-book-list-row">Brak aktywnych rezerwacji.</div>
                        )}
                        {reservations.map((reservation) => (
                            <div
                                className="admin-book-card"
                                key={reservation.idReservation}
                            >
                                <div className="admin-book-card-element"  style={{ flex: 3 }}>
                                    {reservation.idReservation}
                                </div>
                                <div className="admin-book-card-element"  style={{ flex: 3 }}>
                                    {reservation.username || "Brak danych"}
                                </div>
                                <div className="admin-book-card-element"  style={{ flex: 2 }}>
                                    {reservation.bookTitle || "Brak danych"}
                                </div>
                                <div className="admin-book-card-element"  style={{ flex: 2 }}>
                                    {new Date(reservation.startDate).toLocaleString()}
                                </div>
                                <div className="admin-book-card-element" style={{ flex: 1 }}>
                                    <button
                                        onClick={() =>
                                            handleCompleteReservation(reservation.idReservation)
                                        }
                                    >
                                        Przekształć w wypożyczenie
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </>
    );
}

export default ReservationsList;
