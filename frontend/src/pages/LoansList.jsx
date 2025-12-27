import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/AdminPages.css";
import { jwtDecode } from "jwt-decode";

function LoansList() {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [loans, setLoans] = useState([]);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchUser, setSearchUser] = useState("");

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
        const query = new URLSearchParams({
            page: page,
            size: 12,
            username: searchUser ?? ""
        }).toString();

        fetch(`http://localhost:8080/api/v1/checkout/loans?${query}`, {
            headers: { "Authorization": `Bearer ${localStorage.getItem("jwtToken")}` }
        })
            .then((res) => {
                if (!res.ok) throw new Error("Błąd pobierania wypożyczeń");
                return res.json();
            })
            .then(data => {
                setLoans(data.content);
                setTotalPages(data.totalPages);
                setIsLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setIsLoading(false);
            });
    };

    useEffect(() => {
        fetchLoans();
    }, [page, searchUser]);

    const handleReturnLoan = (loanId) => {
        if (!window.confirm("Czy na pewno chcesz oznaczyć wypożyczenie jako zwrócone?")) return;

        fetch("http://localhost:8080/api/v1/checkout/loans/return", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: JSON.stringify({
                loanId: loanId,
                endDate: new Date().toISOString()
            })
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

    const handleSearchKey = (e) => {
        if (e.key === "Enter") {
            setPage(0);
            setSearchUser(e.target.value);
        }
    };

    if (isLoading) {
        return <div className="admin-tools-container">Ładowanie wypożyczeń...</div>;
    }

    if (error) {
        return <div className="admin-tools-container">Wystąpił błąd: {error}</div>;
    }

    return (
        <div className="admin-tools-container">
            <h2 className="admin-title">Lista aktywnych wypożyczeń</h2>

            <div className="admin-search-bar">
                <input
                    type="text"
                    placeholder="Szukaj po użytkowniku..."
                    onKeyDown={handleSearchKey}
                />
                <button onClick={() =>
                    setPage(0) || setSearchUser(document.querySelector(".admin-search-bar input").value)
                }>
                    Szukaj
                </button>
            </div>

            <table className="admin-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Użytkownik</th>
                    <th>Książka</th>
                    <th>Data startowa</th>
                    <th>Termin</th>
                    <th>Akcja</th>
                </tr>
                </thead>
                <tbody>
                {loans.map((l) => (
                    <tr key={l.id}>
                        <td>{l.id}</td>
                        <td>{l.username}</td>
                        <td>{l.bookTitle}</td>
                        <td>{l.startDate.split("T")[0]}</td>
                        <td>{l.dueDate.split("T")[0]}</td>
                        <td>
                            <button
                                className="admin-action-btn return"
                                onClick={() => handleReturnLoan(l.id)}
                            >
                                Zwróć książkę
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <div className="admin-pagination">
                <button
                    disabled={page === 0}
                    onClick={() => setPage(page - 1)}
                >
                    Poprzednia
                </button>

                <span>Strona {page + 1} / {totalPages}</span>

                <button
                    disabled={page + 1 >= totalPages}
                    onClick={() => setPage(page + 1)}
                >
                    Następna
                </button>
            </div>
        </div>
    );
}

export default LoansList;
