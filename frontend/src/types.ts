export interface Owner {
  id: number;
  type: string;
  login: string;
  name: string;
  surname: string;
}
export enum Visibility{
  EVERYONE = 'EVERYONE',
  FRIENDS = 'FRIENDS',
  GROUP = 'GROUP',
}

export interface Group {
  id: number;
  createdAt: string;
  name: string;
  description?: string;
  creatorUserId: number;
}
export enum FriendshipStatus {
  PENDING,
  ACCEPTED,
  REJECTED,
  BLOCKED,
}
export interface GroupMembership {
createdAt: string;
groupId: number;
userId: number;
role: 'OWNER' | 'ADMIN' | 'MEMBER';
}

export interface Friend {
  id: number;
  login: string;
  name: string;
  surname: string;
  email: string;
}

export interface FriendsResponse {
  friends: Friend;
  totalCount: number;
}

export type User = {
  id: number;
  name: string;
  surname: string;
  login: string;
  email: string;
  description?: string;
};

export interface GroupMembership {
  createdAt: string;
  groupId: number;
  userId: number;
  role: 'OWNER' | 'ADMIN' | 'MEMBER';
}

export type Message = {
  id: number
  chatId: number
  senderId: number
  content: string
  createdAt: string
  status?: string // Опциональное поле, так как может не использоваться на фронтенде
}