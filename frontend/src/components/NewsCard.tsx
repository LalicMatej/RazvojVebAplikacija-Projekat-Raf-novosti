import { Card, Badge } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import type { News } from '../types';

interface Props {
    news: News;
}

const NewsCard = ({ news }: Props) => {
    const navigate = useNavigate();

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${String(date.getDate()).padStart(2, '0')}.${String(date.getMonth() + 1).padStart(2, '0')}.${date.getFullYear()}`;
    };

    return (
        <Card
            className="h-100 shadow-sm"
            style={{ cursor: 'pointer', transition: 'transform 0.2s' }}
            onClick={() => navigate(`/news/${news.id}`)}
            onMouseEnter={(e) => (e.currentTarget.style.transform = 'translateY(-4px)')}
            onMouseLeave={(e) => (e.currentTarget.style.transform = 'translateY(0)')}
        >
            <Card.Body className="d-flex flex-column">
                <div className="mb-2">
                    <Badge bg="primary" className="me-1">{news.category.name}</Badge>
                    {news.tags.slice(0, 2).map((tag) => (
                        <Badge key={tag.id} bg="secondary" className="me-1">{tag.name}</Badge>
                    ))}
                </div>

                <Card.Title style={{ fontSize: '1rem', fontWeight: 600 }}>{news.title}</Card.Title>
                <Card.Text className="text-muted flex-grow-1" style={{ fontSize: '0.875rem' }}>
                    {news.content.length > 120 ? `${news.content.slice(0, 120)}...` : news.content}
                </Card.Text>

                <div
                    className="d-flex justify-content-between align-items-center mt-2"
                    style={{ fontSize: '0.8rem', color: '#888' }}
                >
                    <span>{news.author.firstName} {news.author.lastName}</span>
                    <span>{formatDate(news.publishedAt)}</span>
                </div>

                <div className="mt-1" style={{ fontSize: '0.8rem', color: '#aaa' }}>
                    Pregledi: {news.visitCount}
                </div>
            </Card.Body>
        </Card>
    );
};

export default NewsCard;
