import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/BookPages.css";
import BookPageHeader from "../components/BookPageHeader.jsx";
import BookPageCategories from "../components/BookPageCategories.jsx";
import BookPageNav from "../components/BookPageNav.jsx";

const API_URL = "http://localhost:8080/api/v1/books";

function BooksList() {
    const navigate = useNavigate();
    const [books, setBooks] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const PAGE_SIZE = 3;

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

        fetchBooks();
    }, [navigate]);

    const fetchBooks = (params = {}) => {
        const token = localStorage.getItem("jwtToken");

        const query = new URLSearchParams({
            sort: params.sort || "title",
            order: params.order || "asc",
            page: params.page ?? currentPage,
            size: PAGE_SIZE,
            search: params.search || "",
            genre: params.genre || "",
            authors: params.author || "",
        }).toString();

        fetch(`${API_URL}?${query}`, {
            headers: { "Authorization": `Bearer ${token}` },
        })
            .then(res => res.json())
            .then(data => {
                setBooks(data.content);
                setCurrentPage(data.number);
                setTotalPages(data.totalPages);
                setIsLoading(false);
            })
            .catch(error => console.error("Błąd:", error));
    };

    const handleCategoryClick = (sortField) => {
        setIsLoading(true);
        fetchBooks({ sort: sortField, order: "asc" });
    };

    const handlePageChange = (pageNumber) => {
        setIsLoading(true);
        fetchBooks({ page: pageNumber });
    };

    const renderStars = (averageRating) => {
        const fullStars = Math.floor(averageRating || 0);
        const emptyStars = 5 - fullStars;
        return (
            <>
                {Array.from({ length: fullStars }, (_, i) => (
                    <span key={`full-${i}`} className="star full">★</span>
                ))}
                {Array.from({ length: emptyStars }, (_, i) => (
                    <span key={`empty-${i}`} className="star empty">★</span>
                ))}
            </>
        );
    };

    return (
        <div className="container">
            <BookPageHeader />
            <main>
                <h2>Katalog Biblioteki BiblioSolis</h2>
                <BookPageCategories onSortChange={handleCategoryClick} />
                <BookPageNav
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                />

                <section className="book-list">
                    {isLoading ? (
                        <p>Ładowanie książek...</p>
                    ) : (
                        books.map(book => (
                            <div className="book-card" key={book.id}>
                                <div className="book-cover">
                                    <Link to={`/book/${book.id}`}>
                                        {book.cover && (
                                            <img src={`/uploads/${book.cover}`} alt="cover_art" />
                                        )}
                                    </Link>
                                </div>

                                <div className="book-details">
                                    <p>
                                        <strong>Tytuł: </strong>
                                        <Link to={`/book/${book.id}`}>{book.title}</Link>
                                    </p>

                                    <p>
                                        <strong>Autor/rzy:</strong>{" "}
                                        {book.authors && book.authors.length > 0
                                            ? book.authors.join(", ")
                                            : "brak danych"}
                                    </p>

                                    <p>
                                        <strong>Gatunek:</strong> {book.genre || "brak danych"}
                                    </p>

                                    <p className="book-rating">
                                        <strong>Ocena: </strong>
                                        {renderStars(book.averageRating)}
                                        <span className="review-count">
                                            ({book.reviewCount ?? 0})
                                        </span>
                                    </p>
                                </div>

                                <Link to={`/book/${book.id}`}>
                                    <button className="reserve-button">Zobacz</button>
                                </Link>
                            </div>
                        ))
                    )}
                </section>
            </main>
        </div>
    );
}

export default BooksList;
