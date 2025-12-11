import React, { useEffect, useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import BookPageHeader from "../components/BookPageHeader.jsx";

const API_URL = "http://localhost:8080/api/v1/authors";

const AuthorsList = () => {
    const navigate = useNavigate();
    const [authors, setAuthors] = useState([]);
    const [search, setSearch] = useState("");
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);

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
    }, [navigate]);

    const fetchAuthors = () => {
        const url = `${API_URL}?page=${page}&size=${size}&query=${search}`;
        fetch(url, {
            headers: { "Authorization": `Bearer ${localStorage.getItem("jwtToken")}` }
        })
            .then(res => res.json())
            .then(data => {
                if (data && data.content) {
                    setAuthors(data.content);
                    setTotalPages(data.totalPages);
                } else {
                    setAuthors([]);
                    setTotalPages(0);
                }
            })
            .catch(err => {
                console.error(err);
                setAuthors([]);
            });
    };

    useEffect(() => {
        fetchAuthors();
    }, [page, size]);

    const handleSearch = () => {
        setPage(0);
        fetchAuthors();
    };

    const handleAuthorClick = (id) => {
        navigate(`/books?authorId=${id}`);
    };

    return (
        <div className="container">
            <BookPageHeader
                mode="other"
                searchRedirectPath="/books"
            />
            <main>
                <h2>Autorzy</h2>

                <div className="list-search-bar">
                    <input
                        type="text"
                        placeholder="Wyszukaj autora..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    <button onClick={handleSearch}>Szukaj</button>
                </div>

                <div className="entity-list">
                    {authors.map(author => (
                        <div
                            key={author.id}
                            className="entity-card"
                            onClick={() => handleAuthorClick(author.id)}
                            style={{ cursor: "pointer" }}
                        >
                            <div className="entity-main">
                                <span className="entity-title">{author.name} {author.surname}</span>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="page-nav">
                    <button
                        className="prev-page"
                        disabled={page === 0}
                        onClick={() => setPage(prev => prev - 1)}
                    >
                        {"<"}
                    </button>
                    <div className="page-numbers">
                        {[...Array(totalPages)].map((_, i) => (
                            <button
                                key={i}
                                page-active={page === i ? "true" : "false"}
                                onClick={() => setPage(i)}
                            >
                                {i + 1}
                            </button>
                        ))}
                    </div>
                    <button
                        className="next-page"
                        disabled={page === totalPages - 1}
                        onClick={() => setPage(prev => prev + 1)}
                    >
                        {">"}
                    </button>
                </div>
            </main>
        </div>
    );
};

export default AuthorsList;