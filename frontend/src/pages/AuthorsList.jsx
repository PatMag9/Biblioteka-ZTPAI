import React from "react";
import { useNavigate } from "react-router-dom";

function AuthorsList() {
    const navigate = useNavigate();

    const authors = [
        { id: 1, name: "Autor Pierwszy" },
        { id: 2, name: "Autor Drugi" },
        { id: 3, name: "Autor Trzeci" },
    ];

    const chooseAuthor = (id) => {
        navigate(`/books?authorId=${id}`);
    };

    return (
        <div>
            <h2>Lista autorów</h2>

            <ul>
                {authors.map(a => (
                    <li
                        key={a.id}
                        style={{ cursor: "pointer", marginBottom: "10px" }}
                        onClick={() => chooseAuthor(a.id)}
                    >
                        {a.name}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default AuthorsList;