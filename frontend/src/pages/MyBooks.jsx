import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Bookpages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";

const API_URL = "http://localhost:8080/api/v1/books";
const API_URL2 = "http://localhost:8080/api/v1/checkout";

export default function MyBooks() {

    const navigate = useNavigate();
    const [items, setItems] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const formatDate = (dateStr) => {
        if (!dateStr) return "-";
        return dateStr.split("T")[0];
    };

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");

        if (!token) {
            alert("Brak tokenu uwierzytelniającego.");
            navigate("/");
            return;
        }

        fetch(`${API_URL}/my`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => {
                setItems(data.content);
                setIsLoading(false);
            })
            .catch(err => console.error("Błąd pobierania:", err));
    }, [navigate]);


    const extendLoan = (loanId) => {
        const token = localStorage.getItem("jwtToken");

        fetch(`${API_URL2}/loans/${loanId}/extend`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) throw new Error("Błąd podczas przedłużania");
                return res.text();
            })
            .then(() => {
                alert("Wypożyczenie przedłużone.");
                window.location.reload();
            })
            .catch(err => alert(err.message));
    };


    const cancelReservation = (reservationId) => {
        const token = localStorage.getItem("jwtToken");

        fetch(`${API_URL2}/reservations/${reservationId}/cancel`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) throw new Error("Błąd anulowania rezerwacji");
                return res.text();
            })
            .then(() => {
                alert("Rezerwacja anulowana.");
                window.location.reload();
            })
            .catch(err => alert(err.message));
    };


    return (
        <div className="container">
            <BookPageHeader
                mode="other"
                searchRedirectPath="/books"
            />
            <div className="mybooks-container">

                <h2>Moje książki</h2>

                {isLoading ? (
                    <p>Ładowanie...</p>
                ) : (
                    <table className="mybooks-table">
                        <thead>
                        <tr>
                            <th>Okładka</th>
                            <th>Tytuł</th>
                            <th>Typ</th>
                            <th>Termin</th>
                            <th>Akcja</th>
                        </tr>
                        </thead>

                        <tbody>
                        {items.map((item) => (
                            <tr key={item.itemId}>
                                <td className="cover-cell">
                                    <Link to={`/book/${item.bookId}`}>
                                        <img
                                            src={`/uploads/${item.cover}`}
                                            alt="cover"
                                            className="mybooks-cover"
                                        />
                                    </Link>
                                </td>

                                <td>
                                    <Link className="title-link" to={`/book/${item.bookId}`}>
                                        {item.title}
                                    </Link>
                                </td>

                                <td>
                                    {item.type === "loan" ? "Wypożyczenie" : "Rezerwacja"}
                                </td>

                                <td>{formatDate(item.dueDate)}</td>

                                <td>
                                    {item.type === "loan" ? (
                                        item.canExtend ? (
                                            <button
                                                className="action-btn extend-btn"
                                                onClick={() => extendLoan(item.itemId)}
                                            >
                                                Przedłuż termin
                                            </button>
                                        ) : (
                                            <span className="extend-disabled">Nie można przedłużyć</span>
                                        )
                                    ) : (
                                        <button
                                            className="action-btn cancel-btn"
                                            onClick={() => cancelReservation(item.itemId)}
                                        >
                                            Anuluj
                                        </button>
                                    )}
                                </td>

                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
