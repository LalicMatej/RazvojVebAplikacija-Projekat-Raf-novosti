import _axios from './axiosInstance';
import type {LoginRequest, LoginResponse} from '../types';

export const login = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await _axios.post<LoginResponse>('/api/auth/login', data);
    return response.data;
};

export const logoutApi = async (): Promise<void> => {
    await _axios.post('/api/auth/logout');
};