import {useAuth} from "../context/AuthContext"
  
export const joinEvent = async (eventId: number): Promise<void> => {
    const userJson = localStorage.getItem("user");
    const token = localStorage.getItem("token");
    
    if (!userJson || !token) {
        throw new Error("Требуется авторизация");
    }

    const user = JSON.parse(userJson);
    if (!user?.id) {
        throw new Error("Неверные данные пользователя");
    }

    const payload = {
        eventId,
        userId: user.id
    };

    const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
    const response = await fetch(`${API_BASE_URL}/event-members`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || "Ошибка при добавлении к мероприятию");
    }
};