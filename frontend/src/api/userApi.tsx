import { User } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const makeRequest = async (
  endpoint: string,
  method: string = 'GET',
  token?: string | null,
  body?: any
) => {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  if (token) { 
    headers['Authorization'] = `Bearer ${token}`;
  } else {
    throw new Error("Token is required");
  }

  const response = await fetch(`${API_BASE_URL}/users${endpoint}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => null);
    throw new Error(errorData?.message || 'Request failed');
  }

  return response.json();
};

export const userApi = {
  getCurrentUser: async (userId: number, token: string | null): Promise<User> => {
    return await makeRequest(`/${userId}`, 'GET', token);
  },

  getUserById: async (id: number, token: string | null): Promise<User> => {
    return await makeRequest(`/${id}`, 'GET', token);
  },

  updateCurrentUser: async (userId: number, data: any, token: string | null): Promise<User> => {
    return await makeRequest(`/${userId}`, 'PUT', token, data);
  },

  getAllUsers: async (token: string | null): Promise<User[]> => {
    return await makeRequest('', 'GET', token);
  },

  searchUsers: async (query: string, token: string | null): Promise<User[]> => {
    return await makeRequest(`/search?query=${encodeURIComponent(query)}`, 'GET', token);
  },

  makeFriendRequest: async (
    request: {
      userId: number;
      friendId: number;
      status?: string;
    },
    token?: string | null
  ): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/users/friends/requests`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        ...request,
        status: request.status || 'PENDING',
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Ошибка при отправке заявки: ${errorText}`);
    }
  },
};
