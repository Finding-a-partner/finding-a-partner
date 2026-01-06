import React, { useState } from "react";
import { createGroup } from "../api/groupService";
import { useNavigate } from "react-router-dom";

const CreateGroupPage: React.FC = () => {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();
    const creatorUserId = Number(localStorage.getItem("userId") || "0");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!name) {
            setError("Название обязательно");
            return;
        }
        try {
            await createGroup({ name, description, creatorUserId });
            navigate("/groups");
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <div>
            <h1>Создать группу</h1>
            {error && <p style={{ color: "red" }}>{error}</p>}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Название</label>
                    <br />
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>
                <div>
                    <label>Описание</label>
                    <br />
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </div>
                <button type="submit">Создать</button>
            </form>
        </div>
    );
};

export default CreateGroupPage;
