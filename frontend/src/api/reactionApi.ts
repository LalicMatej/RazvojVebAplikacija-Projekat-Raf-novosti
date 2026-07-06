import _axios from './axiosInstance';
import type { ReactionResponse, ReactionType } from '../types';

export const getNewsReactions = async (
    newsId: number
): Promise<ReactionResponse> => {
    const response = await _axios.get<ReactionResponse>(
        `/api/news/${newsId}/reactions`
    );
    return response.data;
};

export const reactToNews = async (
    newsId: number,
    reactionType: ReactionType
): Promise<ReactionResponse> => {
    const response = await _axios.post<ReactionResponse>(
        `/api/news/${newsId}/reactions`,
        { reactionType }
    );
    return response.data;
};

export const getCommentReactions = async (
    newsId: number,
    commentId: number
): Promise<ReactionResponse> => {
    const response = await _axios.get<ReactionResponse>(
        `/api/news/${newsId}/comments/${commentId}/reactions`
    );
    return response.data;
};

export const reactToComment = async (
    newsId: number,
    commentId: number,
    reactionType: ReactionType
): Promise<ReactionResponse> => {
    const response = await _axios.post<ReactionResponse>(
        `/api/news/${newsId}/comments/${commentId}/reactions`,
        { reactionType }
    );
    return response.data;
};