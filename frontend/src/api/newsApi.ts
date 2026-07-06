import _axios from './axiosInstance';
import type {News, PageResponse} from '../types';

export const getLatestNews = async (): Promise<News[]> => {
    const response = await _axios.get<News[]>('/api/news/latest');
    return response.data;
};

export const getMostReadNews = async (): Promise<News[]> => {
    const response = await _axios.get<News[]>('/api/news/most-read');
    return response.data;
};

export const getMostReactedNews = async (): Promise<News[]> => {
    const response = await _axios.get<News[]>('/api/news/most-reacted');
    return response.data;
};

export const getNewsByPage = async (page: number): Promise<PageResponse<News>> => {
    const response = await _axios.get<PageResponse<News>>(`/api/news?page=${page}`);
    return response.data;
};

export const getNewsById = async (id: number): Promise<News> => {
    const response = await _axios.get<News>(`/api/news/${id}`);
    return response.data;
};

export const getRelatedNews = async (id: number): Promise<News[]> => {
    const response = await _axios.get<News[]>(`/api/news/${id}/related`);
    return response.data;
};

export const searchNews = async (query: string, page = 1): Promise<PageResponse<News>> => {
    const response = await _axios.get<PageResponse<News>>(`/api/news/search?query=${query}&page=${page}`);
    return response.data;
};

export const getNewsByCategory = async (categoryId: number, page = 1): Promise<PageResponse<News>> => {
    const response = await _axios.get<PageResponse<News>>(`/api/news/by-category/${categoryId}?page=${page}`);
    return response.data;
};

export const getNewsByTag = async (tagId: number, page = 1): Promise<PageResponse<News>> => {
    const response = await _axios.get<PageResponse<News>>(`/api/news/by-tag/${tagId}?page=${page}`);
    return response.data;
};

export interface NewsRequest {
    title: string;
    content: string;
    categoryId: number;
    tags: { name: string }[];
}

export const createNews = async (data: NewsRequest): Promise<News> => {
    const response = await _axios.post<News>('/api/news', data);
    return response.data;
};

export const updateNews = async (id: number, data: NewsRequest): Promise<News> => {
    const response = await _axios.put<News>(`/api/news/${id}`, data);
    return response.data;
};

export const deleteNews = async (id: number): Promise<void> => {
    await _axios.delete(`/api/news/${id}`);
};
