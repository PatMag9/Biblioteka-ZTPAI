import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/AdminPages.css";
import { jwtDecode } from "jwt-decode";
import BookPageHeader from "../components/BookPageHeader.jsx";

function LoansList() {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [loans, setLoans] = useState([]);
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
                navigate("/");
            }
        } catch (err) {
            console.error("Błąd dekodowania tokena:", err);
            navigate("/");
        }
    }, [navigate]);

    const fetchLoans = () => {
        setIsLoading(true);
        fetch("http://localhost:8080/api/v1/checkout/loans", {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((res) => {
                if (!res.ok) throw new Error("Błąd pobierania wypożyczeń");
                return res.json();
            })
            .then((data) => {
                setLoans(data);
                setIsLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setIsLoading(false);
            });
    };

    useEffect(() => {
        fetchLoans();
    }, []);

    const handleReturnLoan = (loanId) => {
        if (!window.confirm("Czy na pewno chcesz oznaczyć wypożyczenie jako zwrócone?")) return;

        fetch(`http://localhost:8080/api/v1/checkout/loans/${loanId}/return`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then((res) => {
                if (!res.ok) throw new Error("Nie udało się zwrócić książki");
                return res.text();
            })
            .then((message) => {
                alert(message);
                fetchLoans();
            })
            .catch((err) => {
                alert("Błąd: " + err.message);
            });
    };

    if (isLoading) {
        return <div className="admin-container">Ładowanie wypożyczeń...</div>;
    }

    if (error) {
        return <div className="admin-container">Wystąpił błąd: {error}</div>;
    }

    return (
        <div className="admin-container">
            <BookPageHeader/>
            <h1>Wypożyczenia</h1>
            <div className="admin-book-list">
                <div className="admin-book-list-header">
                    <div className="admin-header-element">Id wypożyczenia</div>
                    <div className="admin-header-element">Użytkownik</div>
                    <div className="admin-header-element">Książka</div>
                    <div className="admin-header-element">Data wypożyczenia</div>
                    <div className="admin-header-element">Data do zwrotu</div>
                    <div className="admin-header-element">Akcja</div>
                </div>
                <div className="admin-book-list-body">
                    {loans.length === 0 ? (
                        <div className="admin-book-list-row">Brak aktywnych wypożyczeń.</div>
                    ) : (
                        loans.map((loan) => (
                            <div className="admin-book-card" key={loan.idLoan}>
                                <div className="admin-book-card-element">{loan.idLoan}</div>
                                <div className="admin-book-card-element">
                                    {loan.username || "Brak danych"}
                                </div>
                                <div className="admin-book-card-element">
                                    {loan.bookTitle || "Brak danych"}
                                </div>
                                <div className="admin-book-card-element">
                                    {new Date(loan.startDate).toLocaleString()}
                                </div>
                                <div className="admin-book-card-element">
                                    {loan.dueDate
                                        ? new Date(loan.dueDate).toLocaleDateString()
                                        : "-"}
                                </div>
                                <div className="admin-book-card-element">
                                    <button onClick={() => handleReturnLoan(loan.idLoan)}>
                                        Zwróć książkę
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}

export default LoansList;
