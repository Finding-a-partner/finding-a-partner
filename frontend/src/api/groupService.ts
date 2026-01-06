import { Group } from "../types";
import { getCurrentUserId } from '../utils/jwt'; 

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const token = localStorage.getItem("token") || "";

export const getAllGroups = async (): Promise<Group[]> => {
    const response = await fetch(`${BASE_URL}/groups`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения списка групп");
    }
    return await response.json();
};

export const getGroupsByUserId = async (): Promise<Group[]> => {
    const userId = getCurrentUserId();
    const response = await fetch(`${BASE_URL}/groups/${userId}/group`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения списка групп");
    }
    return await response.json();
};

export const getGroupsByOwnerId = async (): Promise<Group[]> => {
    const userId = getCurrentUserId();
    const response = await fetch(`${BASE_URL}/groups/${userId}/owner`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения списка групп");
    }
    return await response.json();
};

export const getGroupById = async (id: number): Promise<Group> => {
    const response = await fetch(`${BASE_URL}/groups/${id}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка получения данных группы");
    }
    return await response.json();
};

export const createGroup = async (
    group: Omit<Group, "id" | "createdAt">
): Promise<Group> => {
    const response = await fetch(`${BASE_URL}/groups`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-Id": `${group.creatorUserId}`,
        },
        body: JSON.stringify(group),
    });
    if (!response.ok) {
        throw new Error("Ошибка создания группы");
    }
    return await response.json();
};

export const updateGroup = async (
    id: number,
    group: Partial<Omit<Group, "id" | "createdAt">>
): Promise<Group> => {
    const response = await fetch(`${BASE_URL}/groups/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(group),
    });
    if (!response.ok) {
        throw new Error("Ошибка обновления группы");
    }
    return await response.json();
};

export const deleteGroup = async (id: number): Promise<void> => {
    const response = await fetch(`${BASE_URL}/groups/${id}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
    });
    if (!response.ok) {
        throw new Error("Ошибка удаления группы");
    }
};
