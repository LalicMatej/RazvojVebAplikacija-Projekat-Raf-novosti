import _axios from './axiosInstance';
import type { Category, PageResponse } from '../types';

export interface CategoryRequest {
    name: string;
    description: string;
}

export const getCategoriesPage = async (page = 1): Promise<PageResponse<Category>> => {
    const response = await _axios.get<PageResponse<Category>>(`/api/categories?page=${page}`);
    return response.data;
};

export const getAllCategories = async (page = 1): Promise<Category[]> => {
    const response = await getCategoriesPage(page);
    return response.data;
};

export const createCategory = async (data: CategoryRequest): Promise<Category> => {
    const response = await _axios.post<Category>('/api/categories', data);
    return response.data;
};

export const updateCategory = async (
    id: number,
    data: CategoryRequest
): Promise<Category> => {
    const response = await _axios.put<Category>(`/api/categories/${id}`, data);
    return response.data;
};

export const deleteCategory = async (id: number): Promise<void> => {
    await _axios.delete(`/api/categories/${id}`);
};
