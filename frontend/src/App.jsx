import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import BooksList from "./BookList.jsx";
import BookDetails from "./BookDetails.jsx";
import Login from "./Login.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/book/:id" element={<BookDetails />} />
            </Routes>
        </Router>
    );
}

export default App;
