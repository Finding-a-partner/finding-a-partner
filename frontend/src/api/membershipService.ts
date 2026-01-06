import { useAuth } from "../context/AuthContext";
import { GroupMembership, Owner } from "../types";

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
const token = localStorage.getItem("token") || "";

export const getMembershipsByGroupId = async (
    groupId: number
): Promise<GroupMembership[]> => {
    const response = await fetch(`${BASE_URL}/groups/${groupId}/member`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения участников группы");
    }
    return await response.json();
};


export const getMembersByUserId = async (
    userId: number
): Promise<Owner> => {
    const response = await fetch(`${BASE_URL}/users/${userId}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения участников группы");
    }
    return await response.json();
};


export const addMembership = async (
    groupId: number,
): Promise<GroupMembership> => {
    const userJson = localStorage.getItem("user");
    const token = localStorage.getItem("token");
    if (!userJson || !token) {
        throw new Error("Требуется авторизация");
    }

    const user = JSON.parse(userJson);
    const userId = user.id;
    const role = 'MEMBER'
    const response = await fetch(`${BASE_URL}/groups/${groupId}/member`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-Id": `${userId}`,
        },
        body: JSON.stringify({ role }),
    });
    if (!response.ok) {
        throw new Error("Ошибка добавления участника");
    }
    return await response.json();
};

export const updateMembership = async (
    groupId: number,
    userId: number,
    role: "OWNER" | "ADMIN" | "MEMBER"
): Promise<GroupMembership> => {
    const response = await fetch(`${BASE_URL}/groups/${groupId}/member`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-Id": `${userId}`,
        },
        body: JSON.stringify({ role }),
    });
    if (!response.ok) {
        throw new Error("Ошибка обновления участника");
    }
    return await response.json();
};

export const deleteMembership = async (
    groupId: number,
    userId: number
): Promise<void> => {
    const response = await fetch(`${BASE_URL}/groups/${groupId}/member`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-Id": `${userId}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка удаления участника");
    }
};
