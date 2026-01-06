import React, { useEffect, useState } from "react";
import { Group } from "../types";
import { getAllGroups, deleteGroup, getGroupsByUserId, getGroupsByOwnerId } from "../api/groupService";
import { Link, useNavigate } from "react-router-dom";

type GroupTab = 'all' | 'member' | 'owner';

const GroupListPage: React.FC = () => {
    const [groups, setGroups] = useState<Group[]>([]);
    const [activeTab, setActiveTab] = useState<GroupTab>('all');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const fetchGroups = async (tab: GroupTab) => {
        setLoading(true);
        try {
            let data: Group[];
            switch (tab) {
                case 'member':
                    data = await getGroupsByUserId();
                    break;
                case 'owner':
                    data = await getGroupsByOwnerId();
                    break;
                case 'all':
                default:
                    data = await getAllGroups();
                    break;
            }
            setGroups(data);
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchGroups(activeTab);
    }, [activeTab]);

    const handleDelete = async (id: number) => {
        try {
            await deleteGroup(id);
            fetchGroups(activeTab);
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Список групп</h1>
            
            {/* Вкладки */}
            <div style={{ display: 'flex', marginBottom: '20px', borderBottom: '1px solid #e0e0e0' }}>
                <button
                    onClick={() => setActiveTab('all')}
                    style={{
                        padding: '10px 20px',
                        backgroundColor: activeTab === 'all' ? '#f0f0f0' : 'transparent',
                        border: 'none',
                        borderBottom: activeTab === 'all' ? '2px solid #1976d2' : 'none',
                        cursor: 'pointer',
                        fontWeight: activeTab === 'all' ? 'bold' : 'normal'
                    }}
                >
                    Все группы
                </button>
                <button
                    onClick={() => setActiveTab('member')}
                    style={{
                        padding: '10px 20px',
                        backgroundColor: activeTab === 'member' ? '#f0f0f0' : 'transparent',
                        border: 'none',
                        borderBottom: activeTab === 'member' ? '2px solid #1976d2' : 'none',
                        cursor: 'pointer',
                        fontWeight: activeTab === 'member' ? 'bold' : 'normal'
                    }}
                >
                    Мои группы
                </button>
                <button
                    onClick={() => setActiveTab('owner')}
                    style={{
                        padding: '10px 20px',
                        backgroundColor: activeTab === 'owner' ? '#f0f0f0' : 'transparent',
                        border: 'none',
                        borderBottom: activeTab === 'owner' ? '2px solid #1976d2' : 'none',
                        cursor: 'pointer',
                        fontWeight: activeTab === 'owner' ? 'bold' : 'normal'
                    }}
                >
                    Организованные группы
                </button>
            </div>

            {error && <p style={{ color: "red" }}>{error}</p>}
            
            <button 
                onClick={() => navigate("/groups/create")}
                style={{
                    marginBottom: '20px',
                    padding: '10px 15px',
                    backgroundColor: '#1976d2',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                }}
            >
                Создать группу
            </button>

            {loading ? (
                <p>Загрузка...</p>
            ) : (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                    {groups.length > 0 ? (
                        groups.map((group) => (
                            <li 
                                key={group.id}
                                style={{
                                    padding: '15px',
                                    marginBottom: '10px',
                                    border: '1px solid #e0e0e0',
                                    borderRadius: '4px',
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center'
                                }}
                            >
                                <Link 
                                    to={`/groups/${group.id}`}
                                    style={{
                                        textDecoration: 'none',
                                        color: '#333',
                                        fontSize: '18px',
                                        flexGrow: 1
                                    }}
                                >
                                    {group.name}
                                </Link>
                                {activeTab === 'owner' && (
                                    <button
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleDelete(group.id);
                                        }}
                                        style={{
                                            padding: '5px 10px',
                                            backgroundColor: '#f44336',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '4px',
                                            cursor: 'pointer',
                                            marginLeft: '10px'
                                        }}
                                    >
                                        Удалить
                                    </button>
                                )}
                            </li>
                        ))
                    ) : (
                        <p>Нет групп для отображения</p>
                    )}
                </ul>
            )}
        </div>
    );
};

export default GroupListPage;