import _axios from './axiosInstance';
import type { Tag } from '../types';

export const getAllTags = async (): Promise<Tag[]> => {
    const response = await _axios.get<Tag[]>('/api/tags');
    return response.data;
};