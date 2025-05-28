import React, {useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from "jwt-decode";

const AdminPage = () => {
    const navigate = useNavigate();

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

    return (
        <div style={{ padding: 20 }}>
            <h1>Panel Admina</h1>
            <button onClick={() => navigate('/admin/books')} style={{ margin: 10, padding: '10px 20px' }}>
                Książki
            </button>
            <button onClick={() => navigate('/admin/reservations')} style={{ margin: 10, padding: '10px 20px' }}>
                Rezerwacje
            </button>
            <button onClick={() => navigate('/admin/loans')} style={{ margin: 10, padding: '10px 20px' }}>
                Wypożyczenia
            </button>
        </div>
    );
};

export default AdminPage;