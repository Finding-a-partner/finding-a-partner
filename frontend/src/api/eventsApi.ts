import { getCurrentUserId } from '../utils/jwt'; 
import EventsPage from '../pages/EventsPage'
import { Owner, Group, Visibility } from '../types';

// Типы для TypeScript
export type OwnerType = 'USER' | 'GROUP';


export interface EventResponse {
  id: number;
  ownerId: number;
  ownerType: OwnerType;
  title: string;
  visibility: Visibility;
  description: string | null;
  time: string;
  date: string;
}


const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const eventApi = {
    
  /**
   * Получает все мероприятия для текущего пользователя
   */
  getUserEvents: async (): Promise<EventResponse[]> => {
    try {
      const userId = getCurrentUserId(); // Получаем ID из токена
      const response = await fetch(`${API_BASE_URL}/event-members/user/${userId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });
      return response.json()
    } catch (error) {
      console.error('Error fetching user events:', error);
      throw error;
    }
  },
  getOwnerInfo: async (ownerId: number, ownerType: OwnerType): Promise<Owner | Group> => {
    try {
      if (ownerType === 'USER') {
        const response = await fetch(`${API_BASE_URL}/users/${ownerId}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (!response.ok) {
          throw new Error(`Failed to fetch user: ${response.status}`);
        }
        
        return await response.json() as Owner;
      } else {
        const response = await fetch(`${API_BASE_URL}/groups/${ownerId}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (!response.ok) {
          throw new Error(`Failed to fetch group: ${response.status}`);
        }
        
        return await response.json() as Group;
      }
    } catch (error) {
      console.error('Error fetching owner info:', error);
      throw error; // Важно пробросить ошибку дальше
    }
  },
//для пользователя
  getOwnerEvents: async (): Promise<EventResponse[]> => {
    try {
      const ownerId = getCurrentUserId(); // Получаем ID из токена
      const ownerType = "USER"
      const response = await fetch(`${API_BASE_URL}/events/${ownerType}/${ownerId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });
      return response.json()
    } catch (error) {
      console.error('Error fetching user events:', error);
      throw error;
    }
  },



updateEvent: async (
  id: number,
  eventData: {
  title: string;
  description?: string;
  visibility: Visibility;
  time: string;
  date: string;
}): Promise<EventResponse> => {
  try {
    const ownerId = getCurrentUserId(); // Получаем id текущего пользователя
    const ownerType = 'USER'; // Устанавливаем ownerType как 'USER'
    
    const payload = {
      ...eventData,
      ownerId,
      ownerType
    };

    const response = await fetch(`${API_BASE_URL}/events/${id}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create event');
    }

    return response.json();
  } catch (error) {
    console.error('Error creating event:', error);
    throw error;
  }
},


createEvent: async (
  eventData: {
  title: string;
  description?: string;
  visibility: Visibility;
  time: string;
  date: string;
}): Promise<EventResponse> => {
  try {
    const ownerId = getCurrentUserId(); // Получаем id текущего пользователя
    const ownerType = 'USER'; // Устанавливаем ownerType как 'USER'
    
    const payload = {
      ...eventData,
      ownerId,
      ownerType
    };

    const response = await fetch(`${API_BASE_URL}/events`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create event');
    }

    return response.json();
  } catch (error) {
    console.error('Error creating event:', error);
    throw error;
  }
},

deleteEvent : async (id: number): Promise<void> => {
  try {
    const response = await fetch(`${API_BASE_URL}/events/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      }
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to delete event');
    }
  } catch (error) {
    console.error('Error deleting event:', error);
    throw error;
  }
}
 };

export default eventApi;