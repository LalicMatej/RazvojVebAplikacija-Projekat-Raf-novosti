import _axios from './axiosInstance';
import type { User } from '../types';

export interface UserRequest {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: 'ADMIN' | 'CONTENT_CREATOR';
}

export interface UpdateUserRequest {
    firstName: string;
    lastName: string;
    email: string;
    role: 'ADMIN' | 'CONTENT_CREATOR';
}

export const getUsers = async (page: number): Promise<{ data: User[]; totalPages: number; totalCount: number }> => {
    const response = await _axios.get(`/api/users?page=${page}`);
    return response.data;
};

export const getUserById = async (id: number): Promise<User> => {
    const response = await _axios.get<User>(`/api/users/${id}`);
    return response.data;
};

export const createUser = async (data: UserRequest): Promise<User> => {
    const response = await _axios.post<User>('/api/users', data);
    return response.data;
};

export const updateUser = async (id: number, data: UpdateUserRequest): Promise<User> => {
    const response = await _axios.put<User>(`/api/users/${id}`, data);
    return response.data;
};

export const toggleUserStatus = async (id: number): Promise<User> => {
    const response = await _axios.put<User>(`/api/users/${id}/status`);
    return response.data;
};
