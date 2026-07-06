import { useEffect, useState } from 'react';
import { Alert, Card, ListGroup, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { getMostReactedNews } from '../api/newsApi';
import type { News } from '../types';

const TopReactedNewsSidebar = () => {
    const [news, setNews] = useState<News[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            getMostReactedNews()
                .then((data) => {
                    setNews(data);
                    setError('');
                })
                .catch(() => {
                    setError('Greska pri ucitavanju najreaktivnijih vesti.');
                })
                .finally(() => {
                    setLoading(false);
                });
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, []);

    return (
        <Card className="shadow-sm sticky-top top-reacted-sidebar" style={{ top: '80px' }}>
            <Card.Body>
                <Card.Title style={{ fontSize: '1rem' }}>Najvise reakcija</Card.Title>
                {loading ? (
                    <div className="d-flex justify-content-center py-3">
                        <Spinner animation="border" size="sm" />
                    </div>
                ) : error ? (
                    <Alert variant="danger" className="mb-0">
                        {error}
                    </Alert>
                ) : (
                    <ListGroup variant="flush">
                        {news.map((item) => (
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

export default TopReactedNewsSidebar;
