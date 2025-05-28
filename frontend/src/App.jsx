import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import BookDetails from "./pages/BookDetails.jsx";
import Login from "./pages/Login.jsx";
import BooksList from "./pages/BooksList.jsx";
import Home from "./pages/Home.jsx";
import Register from "./pages/Register.jsx";
import AdminPage from "./pages/Admin.jsx";
import BookAdminList from "./pages/BookAdminList.jsx";
import ReservationsList from "./pages/ReservationsList.jsx";
import LoansList from "./pages/LoansList.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/books" element={<BooksList />} />
                <Route path="/book/:id" element={<BookDetails />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/admin/books" element={<BookAdminList />} />
                <Route path="/admin/reservations" element={<ReservationsList />} />
                <Route path="/admin/loans" element={<LoansList />} />
            </Routes>
        </Router>
    );
}

export default App;
