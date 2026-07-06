export interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    role: 'ADMIN' | 'CONTENT_CREATOR';
    status: 'ACTIVE' | 'INACTIVE';
}

export interface Category {
    id: number;
    name: string;
    description: string;
}

export interface Tag {
    id: number;
    name: string;
}

export interface Author {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
}

export interface Comment {
    id: number;
    authorName: string;
    content: string;
    createdAt: string;
    newsId: number;
}

export interface News {
    id: number;
    title: string;
    content: string;
    publishedAt: string;
    visitCount: number;
    category: Category;
    author: Author;
    tags: Tag[];
    comments?: Comment[];
}

export interface PageResponse<T> {
    data: T[];
    page: number;
    pageSize: number;
    totalCount: number;
    totalPages: number;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    jwt: string;
}

export type ReactionType = 'LIKE' | 'DISLIKE';

export interface ReactionResponse {
    likes: number;
    dislikes: number;
    myReaction: ReactionType | null;
}