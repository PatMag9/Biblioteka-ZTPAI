import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import "../styles/AdminPages.css"
import { jwtDecode } from "jwt-decode";

const API_URL = "http://localhost:8080/api/v1/admin/books";

function BookAdminList() {
    const navigate = useNavigate();
    const [books, setBooks] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [search, setSearch] = useState("");
    const [expandedBooks, setExpandedBooks] = useState({});
    const [genres, setGenres] = useState([]);
    const [authors, setAuthors] = useState([]);
    const [publishers, setPublishers] = useState([]);
    const [showCreateBook, setShowCreateBook] = useState(false);
    const [editingBook, setEditingBook] = useState(null);

    const [showCreateCopyForBookId, setShowCreateCopyForBookId] = useState(null);
    const [editingCopy, setEditingCopy] = useState(null);

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

    useEffect(() => {
        fetchBooks();
    }, [page, search]);

    useEffect(() => {
        fetch("http://localhost:8080/api/v1/genres?size=1000", {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => res.json())
            .then(data => setGenres(data.content ?? []));

        fetch("http://localhost:8080/api/v1/authors?size=1000", {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => res.json())
            .then(data => setAuthors(data.content ?? []));

        fetch("http://localhost:8080/api/v1/publishers?size=1000", {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => res.json())
            .then(data => setPublishers(data.content ?? []));
    }, []);

    const fetchBooks = () => {
        const params = new URLSearchParams({
            page,
            size: 12,
        });

        if (search.trim()) {
            params.append("search", search);
        }

        fetch(`${API_URL}?${params.toString()}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => res.json())
            .then(data => {
                setBooks(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(err => console.error(err));
    };

    const handleSearchKey = (e) => {
        if (e.key === "Enter") {
            setPage(0);
            setSearch(e.target.value);
        }
    };

    const handleSearchClick = () => {
        setPage(0);
        setSearch(document.getElementById("bookSearch").value);
    };

    const toggleCopies = (bookId) => {
        setExpandedBooks(prev => ({
            ...prev,
            [bookId]: !prev[bookId],
        }));
    };

    const handleAddBook = (formData) => {
        fetch("http://localhost:8080/api/v1/admin/books", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: formData,
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się dodać książki");
            })
            .then(() => fetchBooks());
    };

    const handleEditBook = (bookId, formData) => {
        fetch(`http://localhost:8080/api/v1/admin/books/${bookId}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: formData,
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się edytować książki");
                return res.text();
            })
            .then(() => fetchBooks());
    };

    const handleDeleteBook = (book) => {
        if (bookHasLoanedCopies(book)) {
            alert("Nie można usunąć książki posiadającej wypożyczone egzemplarze.");
            return;
        }
        if (!window.confirm("Czy na pewno chcesz usunąć tę książkę?")) return;

        fetch(`http://localhost:8080/api/v1/admin/books/${book.id}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się usunąć książki");
                return res.text();
            })
            .then(() => {
                fetchBooks();
            })
            .catch(err => alert(err.message));
    };

    const handleAddCopy = (bookId, data) => {
        fetch(`http://localhost:8080/api/v1/admin/books/${bookId}/copies`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: JSON.stringify(data),
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się dodać egzemplarza");
                return res.json();
            })
            .then(() => fetchBooks());
    };


    const handleEditCopy = (copy, data) => {
        if (copy.loaned) {
            alert("Nie można edytować wypożyczonego egzemplarza.");
            return;
        }
        fetch(`http://localhost:8080/api/v1/admin/books/copies/${copy.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
            body: JSON.stringify(data),
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się edytować egzemplarza");
                return res.json();
            })
            .then(() => fetchBooks());
    };


    const handleDeleteCopy = (copy) => {
        if (copy.loaned) {
            alert("Nie można usunąć wypożyczonego egzemplarza.");
            return;
        }
        if (!window.confirm("Czy na pewno chcesz usunąć ten egzemplarz?")) return;

        fetch(`http://localhost:8080/api/v1/admin/books/copies/${copy.id}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
            },
        })
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się usunąć egzemplarza");
                return res.text();
            })
            .then(() => {
                fetchBooks();
            })
            .catch(err => alert(err.message));
    };

    const getCopyStatus = (copy) => {
        if (copy.loaned) return "Wypożyczona";
        if (copy.reserved) return "Zarezerwowana";
        return "Dostępna";
    };

    const isCopyLoaned = (copy) => {
        return copy.loaned === true;
    };

    const bookHasLoanedCopies = (book) => {
        return book.copies?.some(c => c.loaned === true);
    };

    return (
        <div className="admin-container-books">
            <h2>Panel administratora – książki</h2>

            <div className="admin-search">
                <input
                    id="bookSearch"
                    type="text"
                    placeholder="Szukaj książki..."
                    onKeyDown={handleSearchKey}
                />
                <button onClick={handleSearchClick}>Szukaj</button>
                <button className="primary" onClick={() => setShowCreateBook(true)}>
                    + Dodaj książkę
                </button>
            </div>

            {showCreateBook && (
                <BookForm
                    genres={genres}
                    authors={authors}
                    onSubmit={(data) => {
                        handleAddBook(data);
                        setShowCreateBook(false);
                    }}
                    onCancel={() => setShowCreateBook(false)}
                />
            )}

            <div className="admin-books-list">
                {books.map(book => (
                    <div key={book.id} className="admin-book-card">
                        <div className="book-header">
                            <img
                                src={`http://localhost:8080/covers/${book.cover}`}
                                alt={book.title}
                                className="book-cover"
                                loading="lazy"
                            />

                            <div className="book-main-info">
                                <h3>{book.title}</h3>
                                <p><strong>Gatunek:</strong> {book.genre?.name}</p>
                                <p>
                                    <strong>Autorzy:</strong>{" "}
                                    {book.authors.map(a => `${a.name} ${a.surname ?? ""}`).join(", ")}
                                </p>
                            </div>

                            <div className="book-actions">
                                <button onClick={() => setEditingBook(book)}>
                                    Edytuj
                                </button>
                                <button
                                    className="danger"
                                    disabled={bookHasLoanedCopies(book)}
                                    title={bookHasLoanedCopies(book)
                                        ? "Nie można usunąć książki z aktywnymi wypożyczeniami"
                                        : ""}
                                    onClick={() => handleDeleteBook(book)}
                                >
                                    Usuń
                                </button>
                            </div>
                        </div>


                        {editingBook?.id === book.id && (
                            <BookForm
                                book={editingBook}
                                genres={genres}
                                authors={authors}
                                onSubmit={(data) => {
                                    handleEditBook(book.id, data);
                                    setEditingBook(null);
                                }}
                                onCancel={() => setEditingBook(null)}
                            />
                        )}

                        <p className="book-description">{book.description}</p>

                        <div className="copies-section">
                            <button
                                className="toggle-copies"
                                onClick={() => toggleCopies(book.id)}
                            >
                                {expandedBooks[book.id]
                                    ? "▲ Ukryj egzemplarze"
                                    : "▼ Pokaż egzemplarze"}
                            </button>

                            {expandedBooks[book.id] && (
                                <div className="copies-list">
                                    <button
                                        className="secondary"
                                        onClick={() => setShowCreateCopyForBookId(book.id)}
                                    >
                                        + Dodaj egzemplarz
                                    </button>

                                    {showCreateCopyForBookId === book.id && (
                                        <BookCopyForm
                                            publishers={publishers}
                                            onSubmit={(data) => {
                                                handleAddCopy(book.id, data);
                                                setShowCreateCopyForBookId(null);
                                            }}
                                            onCancel={() => setShowCreateCopyForBookId(null)}
                                        />
                                    )}

                                    {book.copies.map(copy => (
                                        <div key={copy.id}
                                             className={`copy-row status-${getCopyStatus(copy).toLowerCase()}`}>
                                            <span>ID: {copy.id}</span>
                                            <span>Kod książki: {copy.code}</span>
                                            <span>Wydawnictwo: {copy.publisher?.name}</span>
                                            <span>Rok: {copy.yearPublished}</span>
                                            <span className="copy-status">
                                                Status: {getCopyStatus(copy)}
                                            </span>

                                            <div className="copy-actions">
                                                <button
                                                    disabled={isCopyLoaned(copy)}
                                                    title={isCopyLoaned(copy) ? "Egzemplarz jest wypożyczony" : ""}
                                                    onClick={() => setEditingCopy(copy)}
                                                >
                                                    Edytuj
                                                </button>
                                                <button
                                                    className="danger"
                                                    disabled={isCopyLoaned(copy)}
                                                    title={isCopyLoaned(copy) ? "Nie można usunąć wypożyczonego egzemplarza" : ""}
                                                    onClick={() => handleDeleteCopy(copy)}
                                                >
                                                    Usuń
                                                </button>
                                            </div>
                                            {editingCopy?.id === copy.id && (
                                                <BookCopyForm
                                                    copy={editingCopy}
                                                    publishers={publishers}
                                                    onSubmit={(data) => {
                                                        handleEditCopy(copy, data);
                                                        setEditingCopy(null);
                                                    }}
                                                    onCancel={() => setEditingCopy(null)}
                                                />
                                            )}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            <div className="pagination">
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

export default BookAdminList;

function BookForm({ book, genres, authors, onSubmit, onCancel }) {
    const [title, setTitle] = useState(book?.title || "");
    const [description, setDescription] = useState(book?.description || "");
    const [genreId, setGenreId] = useState(book?.genre?.id || "");
    const [authorIds, setAuthorIds] = useState(book?.authors?.map(a => a.id) || []);
    const [coverFile, setCoverFile] = useState(null);
    const [authorSearch, setAuthorSearch] = useState("");

    const toggleAuthor = (id) => {
        setAuthorIds(prev =>
            prev.includes(id)
                ? prev.filter(a => a !== id)
                : [...prev, id]
        );
    };

    const handleSubmit = () => {
        const formData = new FormData();

        formData.append(
            "data",
            new Blob(
                [
                    JSON.stringify({
                        title,
                        description,
                        genreId,
                        authorIds
                    })
                ],
                { type: "application/json" }
            )
        );

        if (coverFile) {
            formData.append("cover", coverFile);
        }

        onSubmit(formData);
    };

    const filteredAuthors = authors.filter(a =>
        `${a.name} ${a.surname ?? ""}`.toLowerCase().includes(authorSearch.toLowerCase())
        && !authorIds.includes(a.id)
    );

    return (
        <div className="admin-form">
            <h3>{book ? "Edytuj książkę" : "Dodaj książkę"}</h3>

            <input
                type="file"
                accept="image/*"
                onChange={e => setCoverFile(e.target.files[0])}
            />

            <input value={title} onChange={e => setTitle(e.target.value)} placeholder="Tytuł"/>

            <select value={genreId} onChange={e => setGenreId(e.target.value)}>
                <option value="">-- wybierz gatunek --</option>
                {genres.map(g => (
                    <option key={g.id} value={g.id}>{g.name}</option>
                ))}
            </select>

            <textarea
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="Opis"
            />

            <div className="authors-active">
                {authorIds.map(id => {
                    const author = authors.find(a => a.id === id);
                    return (
                        <span key={id} className="author-tag">
                            {author.name} {author.surname}
                            <button type="button" onClick={() => toggleAuthor(id)}>×</button>
                        </span>
                    );
                })}
            </div>

            <input
                type="text"
                placeholder="Dodaj autora..."
                value={authorSearch}
                onChange={e => setAuthorSearch(e.target.value)}
            />
            <div className="authors-suggestions">
                {filteredAuthors.slice(0, 5).map(a => (
                    <div
                        key={a.id}
                        className="author-suggestion"
                        onClick={() => toggleAuthor(a.id)}
                    >
                        {a.name} {a.surname}
                    </div>
                ))}
            </div>

            <div className="form-actions">
                <button onClick={handleSubmit}>Zapisz</button>
                <button onClick={onCancel}>Anuluj</button>
            </div>
        </div>
    );
}

function BookCopyForm({copy, publishers, onSubmit, onCancel}) {
    const [code, setCode] = useState(copy?.code || "");
    const [publisherId, setPublisherId] = useState(copy?.publisher?.id || "");
    const [year, setYear] = useState(copy?.yearPublished || "");

    const handleSubmit = () => {
        onSubmit({
            code,
            publisherId,
            yearPublished: year
        });
    };

    return (
        <div className="admin-form">
            <h4>{copy ? "Edytuj egzemplarz" : "Dodaj egzemplarz"}</h4>

            <input value={code} onChange={e => setCode(e.target.value)} placeholder="Code" />

            <select value={publisherId} onChange={e => setPublisherId(e.target.value)}>
                <option value="">-- wybierz wydawnictwo --</option>
                {publishers.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                ))}
            </select>

            <input
                type="number"
                value={year}
                onChange={e => setYear(e.target.value)}
                placeholder="Rok wydania"
            />

            <div className="form-actions">
                <button onClick={handleSubmit}>Zapisz</button>
                <button onClick={onCancel}>Anuluj</button>
            </div>
        </div>
    );
}

