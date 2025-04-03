import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import BooksList from "./BookList.jsx";
import BookDetails from "./BookDetails.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<BooksList />} />
                <Route path="/book/:id" element={<BookDetails />} />
            </Routes>
        </Router>
    );
}

export default App;
