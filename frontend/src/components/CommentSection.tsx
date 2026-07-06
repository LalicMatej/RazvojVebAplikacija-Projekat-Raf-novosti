import { useState, useEffect, useCallback } from 'react';
import {
    Card, Button, Form, Pagination,
    Alert, Spinner, Badge
} from 'react-bootstrap';
import type { Comment, PageResponse, ReactionResponse, ReactionType } from '../types';
import {
    getCommentsByNews,
    createComment,
    deleteComment,
    type CommentRequest
} from '../api/commentApi';
import { getCommentReactions, reactToComment } from '../api/reactionApi';
import { isAdmin } from '../auth';
import ReactionBar from './ReactionBar';
import * as React from "react";
import axios from 'axios';

interface Props {
    newsId: number;
}

interface CommentWithReactions extends Comment {
    reactions?: ReactionResponse;
}

const CommentSection = ({ newsId }: Props) => {
    const [page, setPage]               = useState<PageResponse<Comment> | null>(null);
    const [comments, setComments]       = useState<CommentWithReactions[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading]         = useState(true);
    const [submitting, setSubmitting]   = useState(false);
    const [error, setError]             = useState('');
    const [authorName, setAuthorName]   = useState('');
    const [content, setContent]         = useState('');

    const fetchComments = useCallback(async (pageToLoad: number) => {
        setLoading(true);
        try {
            const data = await getCommentsByNews(newsId, pageToLoad);
            setPage(data);

            const withReactions = await Promise.all(
                data.data.map(async (comment) => {
                    try {
                        const reactions = await getCommentReactions(newsId, comment.id);
                        return { ...comment, reactions };
                    } catch {
                        return comment;
                    }
                })
            );
            setComments(withReactions);
        } catch {
            setError('Greska pri ucitavanju komentara.');
        } finally {
            setLoading(false);
        }
    }, [newsId]);

    useEffect(() => {
        const load = () => {
            fetchComments(currentPage).catch(() => {});
        };
        load();
    }, [fetchComments, currentPage]);
    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault();
        if (!authorName.trim() || !content.trim()) return;
        setSubmitting(true);
        try {
            const data: CommentRequest = { authorName, content };
            await createComment(newsId, data);
            setAuthorName('');
            setContent('');
            setError('');
            setCurrentPage(1);
            await fetchComments(1);
        } catch (error) {
            const message = axios.isAxiosError(error)
                ? error.response?.data?.message
                : null;
            setError(message || 'Greska pri dodavanju komentara.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async (commentId: number) => {
        if (!window.confirm('Obrisati komentar?')) return;
        try {
            await deleteComment(newsId, commentId);
            await fetchComments(currentPage);
        } catch {
            setError('Greska pri brisanju komentara.');
        }
    };

    const handleCommentReact = async (
        commentId: number,
        type: ReactionType
    ) => {
        const updated = await reactToComment(newsId, commentId, type);
        setComments((prev) =>
            prev.map((c) =>
                c.id === commentId ? { ...c, reactions: updated } : c
            )
        );
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return (
            `${String(date.getDate()).padStart(2, '0')}.` +
            `${String(date.getMonth() + 1).padStart(2, '0')}.` +
            `${date.getFullYear()} ` +
            `${String(date.getHours()).padStart(2, '0')}:` +
            `${String(date.getMinutes()).padStart(2, '0')}`
        );
    };

    const renderPagination = () => {
        if (!page || page.totalPages <= 1) return null;
        const items = [];
        for (let i = 1; i <= page.totalPages; i++) {
            items.push(
                <Pagination.Item
                    key={i}
                    active={i === currentPage}
                    onClick={() => setCurrentPage(i)}
                >
                    {i}
                </Pagination.Item>
            );
        }
        return (
            <Pagination size="sm" className="mt-3">
                {items}
            </Pagination>
        );
    };

    return (
        <div className="mt-5">
            <h4 className="border-bottom pb-2 mb-4">
                Komentari{' '}
                {page && <Badge bg="secondary">{page.totalCount}</Badge>}
            </h4>

            {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                    {error}
                </Alert>
            )}

            <Card className="mb-4 shadow-sm">
                <Card.Body>
                    <h6 className="mb-3">Ostavi komentar</h6>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-2">
                            <Form.Control
                                type="text"
                                placeholder="Vase ime"
                                value={authorName}
                                onChange={(e) => setAuthorName(e.target.value)}
                                required
                                maxLength={100}
                            />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Control
                                as="textarea"
                                rows={3}
                                placeholder="Tekst komentara..."
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                required
                                maxLength={2000}
                            />
                        </Form.Group>
                        <Button
                            type="submit"
                            variant="primary"
                            size="sm"
                            disabled={submitting}
                        >
                            {submitting ? 'Slanje...' : 'Posalji'}
                        </Button>
                    </Form>
                </Card.Body>
            </Card>

            {loading ? (
                <div className="d-flex justify-content-center">
                    <Spinner animation="border" size="sm" />
                </div>
            ) : comments.length === 0 ? (
                <p className="text-muted">Nema komentara. Budite prvi!</p>
            ) : (
                comments.map((comment) => (
                    <Card key={comment.id} className="mb-3 shadow-sm">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-start">
                                <div>
                                    <strong>{comment.authorName}</strong>
                                    <span
                                        className="text-muted ms-2"
                                        style={{ fontSize: '0.8rem' }}
                                    >
                    {formatDate(comment.createdAt)}
                  </span>
                                </div>
                                {isAdmin() && (
                                    <Button
                                        variant="outline-danger"
                                        size="sm"
                                        onClick={() => handleDelete(comment.id)}
                                    >
                                        Obrisi
                                    </Button>
                                )}
                            </div>
                            <p className="mt-2 mb-1">{comment.content}</p>
                            {comment.reactions && (
                                <ReactionBar
                                    reactions={comment.reactions}
                                    onReact={(type) => handleCommentReact(comment.id, type)}
                                />
                            )}
                        </Card.Body>
                    </Card>
                ))
            )}

            {renderPagination()}
        </div>
    );
};

export default CommentSection;
