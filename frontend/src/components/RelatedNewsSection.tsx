import { useEffect, useState } from 'react';
import { Alert, Card, ListGroup, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { getRelatedNews } from '../api/newsApi';
import type { News } from '../types';

interface Props {
    newsId: number;
}

const RelatedNewsSection = ({ newsId }: Props) => {
    const [relatedNews, setRelatedNews] = useState<News[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            getRelatedNews(newsId)
                .then(setRelatedNews)
                .catch(() => setRelatedNews([]))
                .finally(() => setLoading(false));
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [newsId]);

    if (loading) {
        return (
            <div className="d-flex justify-content-center py-3">
                <Spinner animation="border" size="sm" />
            </div>
        );
    }

    return (
        <Card className="shadow-sm mt-4">
            <Card.Body>
                <Card.Title style={{ fontSize: '1rem' }}>Procitaj jos...</Card.Title>
                {relatedNews.length === 0 ? (
                    <Alert variant="light" className="mb-0">
                        Nema povezanih vesti.
                    </Alert>
                ) : (
                    <ListGroup variant="flush">
                        {relatedNews.map((item) => (
                            <ListGroup.Item key={item.id} className="px-0">
                                <Link to={`/news/${item.id}`} className="text-decoration-none">
                                    {item.title}
                                </Link>
                            </ListGroup.Item>
                        ))}
                    </ListGroup>
                )}
            </Card.Body>
        </Card>
    );
};

export default RelatedNewsSection;
