import { Message } from "../types"


const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export interface ChatResponse {
  id: number;
  type: string;
  name: string | null;
  createdAt: string;
  eventId: number | null;
}

export const getOrCreatePrivateChat = async (
  otherUserId: number,
  token: string
): Promise<ChatResponse> => {
  const response = await fetch(`${API_BASE_URL}/chats/private/${otherUserId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to get or create private chat: ${response.status}`);
  }

  return response.json();
};

export const getChatById = async (
  chatId: number,
  token: string
): Promise<any> => {
  const response = await fetch(`${API_BASE_URL}/chats/${chatId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to get chat: ${response.status}`);
  }

  return response.json();
};

export const getChatMessages = async (
  chatId: number,
  token: string
): Promise<Message[]> => {
  
  try {
    const response = await fetch(`${API_BASE_URL}/messages/chat/${chatId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`Failed to get messages: ${response.status} - ${errorText}`);
    }

    const messages = await response.json()
    return messages
  } catch (error: any) {
    throw error
  }
};

export const getMyChats = async (
  token: string
): Promise<ChatResponse[]> => {
  const response = await fetch(`${API_BASE_URL}/chats/my`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to get my chats: ${response.status}`);
  }

  return response.json();
};

