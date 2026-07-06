import { useEffect, useState } from 'react';
import { Container, Row, Col, Spinner, Alert } from 'react-bootstrap';
import { getLatestNews, getMostReadNews } from '../api/newsApi';
import type {News} from '../types';
import NewsCard from '../components/NewsCard';
import TopReactedNewsSidebar from '../components/TopReactedNewsSidebar';

const HomePage = () => {
    const [latestNews, setLatestNews]   = useState<News[]>([]);
    const [mostRead, setMostRead]       = useState<News[]>([]);
    const [loading, setLoading]         = useState(true);
    const [error, setError]             = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [latest, popular] = await Promise.all([
                    getLatestNews(),
                    getMostReadNews(),
                ]);
                setLatestNews(latest);
                setMostRead(popular);
            } catch {
                setError('Greška prilikom učitavanja vesti.');
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    if (loading) {
        return (
            <Container className="d-flex justify-content-center mt-5">
                <Spinner animation="border" />
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container className="mt-4 mb-5">
            <Row className="g-4">
                <Col lg={8}>
                    <h2 className="mb-3 border-bottom pb-2">Najnovije vesti</h2>
                    <Row xs={1} sm={2} className="g-4 mb-5">
                        {latestNews.map((news) => (
                            <Col key={news.id}>
                                <NewsCard news={news} />
                            </Col>
                        ))}
                        {latestNews.length === 0 && (
                            <Col><p className="text-muted">Nema vesti.</p></Col>
                        )}
                    </Row>

                    <h2 className="mb-3 border-bottom pb-2">Najpopularnije</h2>
                    <Row xs={1} sm={2} className="g-4 mb-5">
                        {mostRead.map((news) => (
                            <Col key={news.id}>
                                <NewsCard news={news} />
                            </Col>
                        ))}
                        {mostRead.length === 0 && (
                            <Col><p className="text-muted">Nema vesti.</p></Col>
                        )}
                    </Row>
                </Col>
                <Col lg={4}>
                    <TopReactedNewsSidebar />
                </Col>
            </Row>
        </Container>
    );
};

export default HomePage;
