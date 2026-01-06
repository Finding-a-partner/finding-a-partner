import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { Group } from "../types";
import { getGroupById, deleteGroup } from "../api/groupService";
import { getMembersByUserId, getMembershipsByGroupId, addMembership } from "../api/membershipService";
import { GroupMembership, Owner } from "../types";
import {useAuth} from "../context/AuthContext"

const GroupDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [group, setGroup] = useState<Group | null>(null);
    const [owner, setOwner] = useState<Owner | null>(null);
    const [members, setMembers] = useState<Owner[]>([]);
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { user } = useAuth();
    const [joiningGroupId, setJoiningGroupId] = useState<number | null>(null);

    const fetchGroup = async () => {
        if (!id) return;
        try {
            const data = await getGroupById(Number(id));
            setGroup(data);
        } catch (err: any) {
            setError(err.message);
        }
    };

    const fetchMembers = async () => {
        if (!id) return;
        try {
            const memberships = await getMembershipsByGroupId(Number(id));
            const userIds = memberships.map(m => m.userId);
            const membersData = await Promise.all(
                userIds.map(userId => getMembersByUserId(userId))
            );
            setMembers(membersData);
        } catch (err: any) {
            setError(err.message);
        }
    };
    const fetchOwner = async () => {
        if (!id) return;
        try {
            const data = await getGroupById(Number(id));
            const owner = await getMembersByUserId(data.creatorUserId)
            
            setOwner(owner);
        } catch (err: any) {
            setError(err.message);
        }
    };


    useEffect(() => {
        fetchGroup();
        fetchMembers();
        fetchOwner();
    }, [id]);

    const handleDeleteGroup = async () => {
        if (!group) return;
        try {
            await deleteGroup(group.id);
            navigate("/groups");
        } catch (err: any) {
            setError(err.message);
        }
    };

    const handleJoinGroup = async (groupId: number) => {
        setJoiningGroupId(groupId);
        try {
          await addMembership(groupId);
        } catch (err: any) {
          setError(err.message);
        }
      };

    if (!group) return <div>Загрузка...</div>;
    const isCreator = user?.id === group?.creatorUserId;

    return (
        <div>
            <h1>Детали группы: {group.name}</h1>
            {error && <p style={{ color: "red" }}>{error}</p>}
        
            <p>
                <strong>Описание:</strong> {group.description}
            </p>
                <strong>Создатель:</strong>
                <ul style={{ listStyleType: 'none', paddingLeft: 0 }}>
                    
                    {owner?.name} {owner?.surname} (@{owner?.login})
                    
                </ul>
                {(!isCreator) && (
                <div style={{ margin: "20px 0", display: "flex", gap: "10px" }}>
                    <button 
                        onClick={() => handleJoinGroup(group.id)}
                        style={{
                            padding: "8px 16px",
                            backgroundColor: "#1976d2",
                            color: "white",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer"
                        }}
                    >
                        Подписаться
                    </button>
                    </div>
                )}
                {(isCreator) && (
                <div style={{ margin: "20px 0", display: "flex", gap: "10px" }}>
                    <button 
                        onClick={() => navigate(`/groups/${group.id}/edit`)}
                        style={{
                            padding: "8px 16px",
                            backgroundColor: "#1976d2",
                            color: "white",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer"
                        }}
                    >
                        Редактировать группу
                    </button>
                    <button 
                        onClick={handleDeleteGroup}
                        style={{
                            padding: "8px 16px",
                            backgroundColor: "#f44336",
                            color: "white",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer"
                        }}
                    >
                        Удалить группу
                    </button>
                </div>
            )}


        <div>
            <h2>Участники группы</h2>
            {members.length > 0 ? (
                <ul>
                    {members.map(member => (
                        <li key={member.id}>
                            {member.name} {member.surname} (@{member.login})
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Нет участников</p>
            )}
        </div>
                <Link to="/groups">Назад к списку групп</Link>
        </div>
    );
    
};

export default GroupDetailPage;
