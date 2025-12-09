import React from "react";
import { useNavigate } from "react-router-dom";

function GenresList() {
    const navigate = useNavigate();

    const genres = [
        { id: 1, name: "Fantasy" },
        { id: 2, name: "Sci-Fi" },
        { id: 3, name: "Horror" },
    ];

    const chooseGenre = (id) => {
        navigate(`/books?genreId=${id}`);
    };

    return (
        <div>
            <h2>Lista gatunków</h2>

            <ul>
                {genres.map(g => (
                    <li
                        key={g.id}
                        style={{ cursor: "pointer", marginBottom: "10px" }}
                        onClick={() => chooseGenre(g.id)}
                    >
                        {g.name}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default GenresList;
