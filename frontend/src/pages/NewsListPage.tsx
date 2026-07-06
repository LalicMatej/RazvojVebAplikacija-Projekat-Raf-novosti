import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import {
    Alert,
    Button,
    Col,
    Container,
    Pagination,
    Row,
    Spinner,
} from 'react-bootstrap';
import {
    getNewsByCategory,
    getNewsByPage,
    getNewsByTag,
    searchNews,
} from '../api/newsApi';
import { getAllCategories } from '../api/categoryApi';
import { getAllTags } from '../api/tagApi';
import type { Category, News, PageResponse, Tag } from '../types';
import NewsCard from '../components/NewsCard';
import TopReactedNewsSidebar from '../components/TopReactedNewsSidebar';

const NewsListPage = () => {
    const navigate = useNavigate();
    const { categoryId, tagId } = useParams<{ categoryId?: string; tagId?: string }>();
    const [searchParams, setSearchParams] = useSearchParams();
    const [newsPage, setNewsPage] = useState<PageResponse<News> | null>(null);
    const [news, setNews] = useState<News[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [tags, setTags] = useState<Tag[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const currentPage = Math.max(1, Number(searchParams.get('page') ?? '1') || 1);
    const searchQuery = searchParams.get('query')?.trim() ?? '';
    const selectedCategoryId = categoryId ? Number(categoryId) : null;
    const selectedTagId = tagId ? Number(tagId) : null;

    useEffect(() => {
        getAllCategories()
            .then(setCategories)
            .catch(() => {});

        getAllTags()
            .then(setTags)
            .catch(() => {});
    }, []);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            const fetchNews = async () => {
                setLoading(true);
                setError('');
                try {
                    let data: PageResponse<News>;
                    if (selectedTagId !== null) {
                        data = await getNewsByTag(selectedTagId, currentPage);
                    } else if (selectedCategoryId !== null) {
                        data = await getNewsByCategory(selectedCategoryId, currentPage);
                    } else if (searchQuery) {
                        data = await searchNews(searchQuery, currentPage);
                    } else {
                        data = await getNewsByPage(currentPage);
                    }

                    setNewsPage(data);
                    setNews(data.data);
                } catch {
                    setError('Greska prilikom ucitavanja vesti.');
                } finally {
                    setLoading(false);
                }
            };

            fetchNews().catch(() => {});
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [currentPage, searchQuery, selectedCategoryId, selectedTagId]);

    const title = useMemo(() => {
        if (selectedTagId !== null) {
            return `Tag: ${tags.find((tag) => tag.id === selectedTagId)?.name ?? 'Nepoznat tag'}`;
        }
        if (selectedCategoryId !== null) {
            return `Kategorija: ${categories.find((category) => category.id === selectedCategoryId)?.name ?? 'Nepoznata kategorija'}`;
        }
        if (searchQuery) {
            return `Rezultati pretrage za: ${searchQuery}`;
        }
        return 'Sve vesti';
    }, [categories, searchQuery, selectedCategoryId, selectedTagId, tags]);

    const updatePage = (page: number) => {
        const next = new URLSearchParams(searchParams);
        next.set('page', String(page));
        setSearchParams(next);
    };

    const clearSearch = () => {
        navigate('/news');
    };

    const renderPagination = () => {
        if (!newsPage || newsPage.totalPages <= 1) return null;

        const items = [];
        for (let i = 1; i <= newsPage.totalPages; i++) {
            items.push(
                <Pagination.Item
                    key={i}
                    active={i === currentPage}
                    onClick={() => updatePage(i)}
                >
                    {i}
                </Pagination.Item>
            );
        }

        return <Pagination className="justify-content-center mt-4">{items}</Pagination>;
    };

    return (
        <Container className="mt-4 mb-5">
            <Row className="g-4">
                <Col lg={8}>
                    <div className="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                        <h2 className="mb-0">{title}</h2>
                        {(searchQuery || selectedCategoryId !== null || selectedTagId !== null) && (
                            <Button variant="outline-secondary" onClick={clearSearch}>
                                Prikazi sve
                            </Button>
                        )}
                    </div>

                    {loading ? (
                        <div className="d-flex justify-content-center mt-5">
                            <Spinner animation="border" />
                        </div>
                    ) : error ? (
                        <Alert variant="danger">{error}</Alert>
                    ) : news.length === 0 ? (
                        <Alert variant="info">Nema pronadjenih vesti.</Alert>
                    ) : (
                        <>
                            <Row xs={1} sm={2} className="g-4">
                                {news.map((item) => (
                                    <Col key={item.id}>
                                        <NewsCard news={item} />
                                    </Col>
                                ))}
                            </Row>
                            {renderPagination()}
                        </>
                    )}
                </Col>
                <Col lg={4}>
                    <TopReactedNewsSidebar />
                </Col>
            </Row>
        </Container>
    );
};

export default NewsListPage;
