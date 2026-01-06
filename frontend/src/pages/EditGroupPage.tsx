import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Group } from "../types";
import { getGroupById, updateGroup } from "../api/groupService";

const EditGroupPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [group, setGroup] = useState<Group | null>(null);
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const fetchGroup = async () => {
        if (!id) return;
        try {
            const data = await getGroupById(Number(id));
            setGroup(data);
            setName(data.name);
            setDescription(data.description || "");
        } catch (err: any) {
            setError(err.message);
        }
    };

    useEffect(() => {
        fetchGroup();
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!group) return;
        try {
            await updateGroup(group.id, { name, description });
            navigate(`/groups/${group.id}`);
        } catch (err: any) {
            setError(err.message);
        }
    };

    if (!group) return <div>Загрузка...</div>;

    return (
        <div>
            <h1>Редактировать группу</h1>
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
                <button type="submit">Сохранить изменения</button>
            </form>
        </div>
    );
};

export default EditGroupPage;
