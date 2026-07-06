import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import {
    Alert,
    Badge,
    Button,
    Container,
    Form,
    InputGroup,
    Pagination,
    Spinner,
    Table,
} from 'react-bootstrap';
import { deleteNews, getNewsByCategory, getNewsByPage, searchNews } from '../../api/newsApi';
import { getAllCategories } from '../../api/categoryApi';
import type { Category, News, PageResponse } from '../../types';
import { getPayload, isAdmin } from '../../auth';
import NewsFormModal from '../../components/cms/NewsFormModal';

const CMSPage = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const [newsPage, setNewsPage] = useState<PageResponse<News> | null>(null);
    const [news, setNews] = useState<News[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editNews, setEditNews] = useState<News | null>(null);
    const [searchInput, setSearchInput] = useState(searchParams.get('query') ?? '');

    const payload = getPayload();
    const admin = isAdmin();

    const getErrorMessage = (fallback: string, caughtError: unknown) => {
        if (axios.isAxiosError(caughtError)) {
            const message = caughtError.response?.data?.message;
            if (typeof message === 'string' && message.trim()) {
                return message;
            }
        }

        return fallback;
    };

    const currentPage = Math.max(1, Number(searchParams.get('page') ?? '1') || 1);
    const searchQuery = searchParams.get('query')?.trim() ?? '';
    const rawCategoryId = searchParams.get('categoryId');
    const parsedCategoryId = rawCategoryId ? Number(rawCategoryId) : null;
    const selectedCategoryId =
        parsedCategoryId !== null && !Number.isNaN(parsedCategoryId)
            ? parsedCategoryId
            : null;

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            setSearchInput(searchQuery);
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [searchQuery]);

    useEffect(() => {
        getAllCategories()
            .then(setCategories)
            .catch(() => {});
    }, []);

    const selectedCategory = useMemo(
        () => categories.find((category) => category.id === selectedCategoryId) ?? null,
        [categories, selectedCategoryId]
    );

    const fetchNews = useCallback(async () => {
        setLoading(true);
        try {
            let data: PageResponse<News>;
            if (searchQuery) {
                data = await searchNews(searchQuery, currentPage);
            } else if (selectedCategoryId !== null) {
                data = await getNewsByCategory(selectedCategoryId, currentPage);
            } else {
                data = await getNewsByPage(currentPage);
            }

            setNewsPage(data);
            setNews(data.data);
            setError('');
        } catch (caughtError) {
            setNewsPage(null);
            setNews([]);
            setError(getErrorMessage('Greska pri ucitavanju vesti.', caughtError));
        } finally {
            setLoading(false);
        }
    }, [currentPage, searchQuery, selectedCategoryId]);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            fetchNews().catch(() => {});
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [fetchNews]);

    const updateParams = (updates: Record<string, string | null>) => {
        const next = new URLSearchParams(searchParams);

        Object.entries(updates).forEach(([key, value]) => {
            if (!value) {
                next.delete(key);
            } else {
                next.set(key, value);
            }
        });

        setSearchParams(next);
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm('Obrisati vest?')) return;

        try {
            await deleteNews(id);
            await fetchNews();
        } catch (caughtError) {
            setError(getErrorMessage('Greska pri brisanju vesti.', caughtError));
        }
    };

    const handleEdit = (item: News) => {
        setEditNews(item);
        setShowModal(true);
    };

    const handleNew = () => {
        setEditNews(null);
        setShowModal(true);
    };

    const handleSaved = () => {
        fetchNews().catch(() => {});
    };

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        updateParams({
            query: searchInput.trim() || null,
            categoryId: null,
            page: '1',
        });
    };

    const handleReset = () => {
        setSearchInput('');
        setSearchParams(new URLSearchParams());
    };

    const canEdit = (item: News) => admin || item.author.id === payload?.userId;

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return (
            `${String(date.getDate()).padStart(2, '0')}.` +
            `${String(date.getMonth() + 1).padStart(2, '0')}.` +
            `${date.getFullYear()}`
        );
    };

    const renderPagination = () => {
        if (!newsPage || newsPage.totalPages <= 1) return null;

        const items = [];
        for (let i = 1; i <= newsPage.totalPages; i++) {
            items.push(
                <Pagination.Item
                    key={i}
                    active={i === currentPage}
                    onClick={() => updateParams({ page: String(i) })}
                >
                    {i}
                </Pagination.Item>
            );
        }

        return <Pagination className="justify-content-center mt-3">{items}</Pagination>;
    };

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">Vesti</h2>
                    <p className="text-muted mb-0">
                        {searchQuery
                            ? `Rezultati pretrage za: ${searchQuery}`
                            : selectedCategory
                                ? `Filtrirano po kategoriji: ${selectedCategory.name}`
                                : 'Pregled svih vesti u CMS-u.'}
                    </p>
                </div>
                <Button variant="primary" onClick={handleNew}>
                    + Nova vest
                </Button>
            </div>

            <Form onSubmit={handleSearch} className="mb-4">
                <InputGroup>
                    <Form.Control
                        type="text"
                        placeholder="Pretrazi po naslovu ili sadrzaju"
                        value={searchInput}
                        onChange={(e) => setSearchInput(e.target.value)}
                    />
                    <Button type="submit" variant="primary">
                        Trazi
                    </Button>
                    {(searchQuery || selectedCategoryId !== null) && (
                        <Button variant="outline-secondary" onClick={handleReset}>
                            Resetuj
                        </Button>
                    )}
                </InputGroup>
            </Form>

            {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                    {error}
                </Alert>
            )}

            {loading ? (
                <div className="d-flex justify-content-center mt-5">
                    <Spinner animation="border" />
                </div>
            ) : news.length === 0 ? (
                <Alert variant="info">Nema vesti za izabrani prikaz.</Alert>
            ) : (
                <>
                    <Table striped bordered hover responsive>
                        <thead className="table-dark">
                            <tr>
                                <th>Naslov</th>
                                <th>Kategorija</th>
                                <th>Autor</th>
                                <th>Datum</th>
                                <th>Posete</th>
                                <th>Akcije</th>
                            </tr>
                        </thead>
                        <tbody>
                            {news.map((item) => (
                                <tr key={item.id}>
                                    <td>
                                        <Button
                                            variant="link"
                                            className="p-0 text-decoration-none"
                                            onClick={() => navigate(`/news/${item.id}`)}
                                        >
                                            {item.title}
                                        </Button>
                                    </td>
                                    <td>
                                        <Badge bg="primary">{item.category.name}</Badge>
                                    </td>
                                    <td>
                                        {item.author.firstName} {item.author.lastName}
                                    </td>
                                    <td>{formatDate(item.publishedAt)}</td>
                                    <td>{item.visitCount}</td>
                                    <td>
                                        {canEdit(item) && (
                                            <div className="d-flex gap-2">
                                                <Button
                                                    size="sm"
                                                    variant="outline-primary"
                                                    onClick={() => handleEdit(item)}
                                                >
                                                    Izmeni
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="outline-danger"
                                                    onClick={() => handleDelete(item.id)}
                                                >
                                                    Obrisi
                                                </Button>
                                            </div>
                                        )}
                                        {!canEdit(item) && (
                                            <span className="text-muted small">Samo autor ili admin</span>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                    {renderPagination()}
                </>
            )}

            <NewsFormModal
                show={showModal}
                onHide={() => setShowModal(false)}
                onSaved={handleSaved}
                editNews={editNews}
            />
        </Container>
    );
};

export default CMSPage;
