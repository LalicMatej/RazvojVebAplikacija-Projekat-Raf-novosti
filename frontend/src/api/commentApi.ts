import _axios from './axiosInstance';
import type { Comment, PageResponse } from '../types';

export interface CommentRequest {
    authorName: string;
    content: string;
}

export const getCommentsByNews = async (
    newsId: number,
    page: number
): Promise<PageResponse<Comment>> => {
    const response = await _axios.get<PageResponse<Comment>>(
        `/api/news/${newsId}/comments?page=${page}`
    );
    return response.data;
};

export const createComment = async (
    newsId: number,
    data: CommentRequest
): Promise<Comment> => {
    const response = await _axios.post<Comment>(
        `/api/news/${newsId}/comments`,
        data
    );
    return response.data;
};

export const deleteComment = async (
    newsId: number,
    commentId: number
): Promise<void> => {
    await _axios.delete(`/api/news/${newsId}/comments/${commentId}`);
};