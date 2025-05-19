import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import BookDetails from "./BookDetails.jsx";
import Login from "./Login.jsx";
import BooksList from "./BooksList.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/books" element={<BooksList />} />
                <Route path="/book/:id" element={<BookDetails />} />
            </Routes>
        </Router>
    );
}

export default App;
