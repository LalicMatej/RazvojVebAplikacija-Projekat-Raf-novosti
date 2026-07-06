import { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import {
    Container, Row, Col, Badge,
    Spinner, Alert, Button, Card
} from 'react-bootstrap';
import { getNewsById } from '../api/newsApi';
import { getNewsReactions, reactToNews } from '../api/reactionApi';
import type { News, ReactionResponse, ReactionType } from '../types';
import ReactionBar from '../components/ReactionBar';
import CommentSection from '../components/CommentSection';
import RelatedNewsSection from '../components/RelatedNewsSection';
import TopReactedNewsSidebar from '../components/TopReactedNewsSidebar';

const NewsDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [news, setNews]             = useState<News | null>(null);
    const [reactions, setReactions]   = useState<ReactionResponse | null>(null);
    const [loading, setLoading]       = useState(true);
    const [error, setError]           = useState('');

    useEffect(() => {
        if (!id) return;
        const newsId = Number(id);

        const fetchData = async () => {
            setLoading(true);
            try {
                const [newsData, reactionData] = await Promise.all([
                    getNewsById(newsId),
                    getNewsReactions(newsId),
                ]);
                setNews(newsData);
                setReactions(reactionData);
            } catch {
                setError('Vest nije pronađena.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [id]);

    const handleReact = async (type: ReactionType) => {
        if (!id) return;
        const updated = await reactToNews(Number(id), type);
        setReactions(updated);
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${String(date.getDate()).padStart(2, '0')}.${String(
            date.getMonth() + 1
        ).padStart(2, '0')}.${date.getFullYear()} ${String(
            date.getHours()
        ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    };

    if (loading) {
        return (
            <Container className="d-flex justify-content-center mt-5">
                <Spinner animation="border" />
            </Container>
        );
    }

    if (error || !news) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">{error || 'Vest nije pronađena.'}</Alert>
                <Button variant="secondary" onClick={() => navigate(-1)}>
                    ← Nazad
                </Button>
            </Container>
        );
    }

    return (
        <Container className="mt-4 mb-5">
            <Button
                variant="outline-secondary"
                size="sm"
                className="mb-3"
                onClick={() => navigate(-1)}
            >
                ← Nazad
            </Button>

            <Row>
                {/* Glavni sadržaj */}
                <Col lg={8}>
                    <article>
                        {/* Kategorija i tagovi */}
                        <div className="mb-2">
                            <Badge bg="primary" className="me-2">{news.category.name}</Badge>
                            {news.tags.map((tag) => (
                                <Badge
                                    key={tag.id}
                                    as={Link}
                                    to={`/news/tag/${tag.id}`}
                                    bg="secondary"
                                    className="me-1 text-decoration-none"
                                >
                                    {tag.name}
                                </Badge>
                            ))}
                        </div>

                        {/* Naslov */}
                        <h1 className="mb-3" style={{ fontWeight: 700 }}>{news.title}</h1>

                        {/* Meta info */}
                        <div
                            className="text-muted mb-4 d-flex gap-3 flex-wrap"
                            style={{ fontSize: '0.9rem' }}
                        >
                            <span>✍️ {news.author.firstName} {news.author.lastName}</span>
                            <span>📅 {formatDate(news.publishedAt)}</span>
                            <span>👁 {news.visitCount} poseta</span>
                        </div>

                        {/* Sadržaj */}
                        <div
                            className="news-content mb-4"
                            style={{ lineHeight: '1.8', fontSize: '1.05rem' }}
                        >
                            {news.content.split('\n').map((paragraph, i) => (
                                <p key={i}>{paragraph}</p>
                            ))}
                        </div>

                        {/* Reakcije na vest */}
                        {reactions && (
                            <div className="border-top border-bottom py-3 mb-4">
                                <strong className="me-3">Reakcije:</strong>
                                <ReactionBar
                                    reactions={reactions}
                                    onReact={handleReact}
                                />
                            </div>
                        )}
                    </article>

                    {/* Komentari */}
                    <CommentSection newsId={news.id} />
                    <RelatedNewsSection newsId={news.id} />
                </Col>

                {/* Sidebar */}
                <Col lg={4}>
                    <div className="d-flex flex-column gap-4">
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title style={{ fontSize: '1rem' }}>O autoru</Card.Title>
                                <p className="mb-1">
                                    <strong>{news.author.firstName} {news.author.lastName}</strong>
                                </p>
                                <p className="text-muted" style={{ fontSize: '0.85rem' }}>
                                    {news.author.email}
                                </p>
                                <hr />
                                <Card.Title style={{ fontSize: '1rem' }}>Kategorija</Card.Title>
                                <Badge
                                    as={Link}
                                    to={`/news/category/${news.category.id}`}
                                    bg="primary"
                                    className="text-decoration-none"
                                >
                                    {news.category.name}
                                </Badge>
                                {news.tags.length > 0 && (
                                    <>
                                        <hr />
                                        <Card.Title style={{ fontSize: '1rem' }}>Tagovi</Card.Title>
                                        <div className="d-flex flex-wrap gap-1">
                                            {news.tags.map((tag) => (
                                                <Badge
                                                    key={tag.id}
                                                    as={Link}
                                                    to={`/news/tag/${tag.id}`}
                                                    bg="secondary"
                                                    className="text-decoration-none"
                                                >
                                                    {tag.name}
                                                </Badge>
                                            ))}
                                        </div>
                                    </>
                                )}
                            </Card.Body>
                        </Card>
                        <TopReactedNewsSidebar />
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default NewsDetailPage;
