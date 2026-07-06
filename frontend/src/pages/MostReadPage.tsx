import { useEffect, useState } from 'react';
import { Alert, Col, Container, Row, Spinner } from 'react-bootstrap';
import { getMostReadNews } from '../api/newsApi';
import NewsCard from '../components/NewsCard';
import TopReactedNewsSidebar from '../components/TopReactedNewsSidebar';
import type { News } from '../types';

const MostReadPage = () => {
    const [news, setNews] = useState<News[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            getMostReadNews()
                .then((data) => {
                    setNews(data);
                    setError('');
                })
                .catch(() => {
                    setError('Greska pri ucitavanju najcitanijih vesti.');
                })
                .finally(() => {
                    setLoading(false);
                });
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, []);

    return (
        <Container className="mt-4 mb-5">
            <Row className="g-4">
                <Col lg={8}>
                    <h2 className="mb-4 border-bottom pb-2">Najcitanije vesti</h2>
                    {loading ? (
                        <div className="d-flex justify-content-center mt-5">
                            <Spinner animation="border" />
                        </div>
                    ) : error ? (
                        <Alert variant="danger">{error}</Alert>
                    ) : news.length === 0 ? (
                        <Alert variant="info">
                            Trenutno nema najcitanijih vesti za zadati period.
                        </Alert>
                    ) : (
                        <Row xs={1} sm={2} className="g-4">
                            {news.map((item) => (
                                <Col key={item.id}>
                                    <NewsCard news={item} />
                                </Col>
                            ))}
                        </Row>
                    )}
                </Col>
                <Col lg={4}>
                    <TopReactedNewsSidebar />
                </Col>
            </Row>
        </Container>
    );
};

export default MostReadPage;
