import React, {useEffect} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import { jwtDecode } from "jwt-decode";
import BookPageHeader from "../components/BookPageHeader.jsx";

const AdminPage = () => {
    const navigate = useNavigate();

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

    return (
        <div className="admin-container">
            <BookPageHeader mode="other" searchRedirectPath="/books" />

            <main className="admin-main">
                <h2>Panel Administratora</h2>

                <section className="admin-cards">

                    <Link to="/admin/books" className="admin-card">
                        <h3>Lista Książek</h3>
                        <p>Zarządzaj pełnym katalogiem książek.</p>
                    </Link>

                    <Link to="/admin/reservations" className="admin-card">
                        <h3>Lista Rezerwacji</h3>
                        <p>Przeglądaj i kontroluj aktywne rezerwacje.</p>
                    </Link>

                    <Link to="/admin/loans" className="admin-card">
                        <h3>Lista Wypożyczeń</h3>
                        <p>Monitoruj wszystkie wypożyczenia użytkowników.</p>
                    </Link>

                </section>
            </main>
        </div>
    );
};

export default AdminPage;