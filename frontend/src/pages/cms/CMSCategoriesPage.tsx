import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Alert,
    Badge,
    Button,
    Container,
    Form,
    Modal,
    Pagination,
    Spinner,
    Table,
} from 'react-bootstrap';
import {
    createCategory,
    deleteCategory,
    getCategoriesPage,
    updateCategory,
    type CategoryRequest,
} from '../../api/categoryApi';
import type { Category, PageResponse } from '../../types';

const EMPTY_FORM: CategoryRequest = {
    name: '',
    description: '',
};

const CMSCategoriesPage = () => {
    const navigate = useNavigate();
    const [categoriesPage, setCategoriesPage] = useState<PageResponse<Category> | null>(null);
    const [categories, setCategories] = useState<Category[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingCategory, setEditingCategory] = useState<Category | null>(null);
    const [form, setForm] = useState<CategoryRequest>(EMPTY_FORM);
    const [saving, setSaving] = useState(false);
    const [formError, setFormError] = useState('');

    const fetchCategories = useCallback(async () => {
        setLoading(true);
        try {
            const data = await getCategoriesPage(currentPage);
            setCategoriesPage(data);
            setCategories(data.data);
            setError('');
        } catch {
            setError('Greska pri ucitavanju kategorija.');
        } finally {
            setLoading(false);
        }
    }, [currentPage]);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            fetchCategories().catch(() => {});
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [fetchCategories]);

    const handleNew = () => {
        setEditingCategory(null);
        setForm(EMPTY_FORM);
        setFormError('');
        setShowModal(true);
    };

    const handleEdit = (category: Category) => {
        setEditingCategory(category);
        setForm({
            name: category.name,
            description: category.description,
        });
        setFormError('');
        setShowModal(true);
    };

    const handleDelete = async (categoryId: number) => {
        if (!window.confirm('Obrisati kategoriju?')) return;
        try {
            await deleteCategory(categoryId);
            await fetchCategories();
        } catch {
            setError('Kategoriju nije moguce obrisati dok sadrzi vesti ili postoji druga greska.');
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSaving(true);
        setFormError('');
        try {
            if (editingCategory) {
                await updateCategory(editingCategory.id, form);
            } else {
                await createCategory(form);
            }
            setShowModal(false);
            setForm(EMPTY_FORM);
            await fetchCategories();
        } catch {
            setFormError('Greska pri cuvanju kategorije.');
        } finally {
            setSaving(false);
        }
    };

    const renderPagination = () => {
        if (!categoriesPage || categoriesPage.totalPages <= 1) return null;

        const items = [];
        for (let i = 1; i <= categoriesPage.totalPages; i++) {
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

        return <Pagination className="justify-content-center mt-3">{items}</Pagination>;
    };

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">Kategorije</h2>
                    <p className="text-muted mb-0">Upravljanje kategorijama za CMS.</p>
                </div>
                <Button variant="primary" onClick={handleNew}>
                    + Nova kategorija
                </Button>
            </div>

            {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                    {error}
                </Alert>
            )}

            {loading ? (
                <div className="d-flex justify-content-center mt-5">
                    <Spinner animation="border" />
                </div>
            ) : categories.length === 0 ? (
                <Alert variant="info">Nema kategorija.</Alert>
            ) : (
                <>
                    <Table striped bordered hover responsive>
                        <thead className="table-dark">
                            <tr>
                                <th>Naziv</th>
                                <th>Opis</th>
                                <th>Akcije</th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories.map((category) => (
                                <tr key={category.id}>
                                    <td>
                                        <Button
                                            variant="link"
                                            className="p-0 text-decoration-none"
                                            onClick={() => navigate(`/cms/news?categoryId=${category.id}`)}
                                        >
                                            {category.name}
                                        </Button>
                                    </td>
                                    <td>{category.description}</td>
                                    <td>
                                        <div className="d-flex gap-2 align-items-center">
                                            <Badge bg="secondary">ID {category.id}</Badge>
                                            <Button
                                                size="sm"
                                                variant="outline-primary"
                                                onClick={() => handleEdit(category)}
                                            >
                                                Izmeni
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="outline-danger"
                                                onClick={() => handleDelete(category.id)}
                                            >
                                                Obrisi
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                    {renderPagination()}
                </>
            )}

            <Modal show={showModal} onHide={() => setShowModal(false)} backdrop="static">
                <Modal.Header closeButton>
                    <Modal.Title>
                        {editingCategory ? 'Izmeni kategoriju' : 'Nova kategorija'}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {formError && <Alert variant="danger">{formError}</Alert>}
                    <Form onSubmit={handleSubmit} id="category-form">
                        <Form.Group className="mb-3">
                            <Form.Label>Naziv kategorije</Form.Label>
                            <Form.Control
                                type="text"
                                value={form.name}
                                onChange={(e) => setForm((current) => ({
                                    ...current,
                                    name: e.target.value,
                                }))}
                                required
                                maxLength={100}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Opis</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                value={form.description}
                                onChange={(e) => setForm((current) => ({
                                    ...current,
                                    description: e.target.value,
                                }))}
                                required
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Otkazi
                    </Button>
                    <Button type="submit" form="category-form" disabled={saving}>
                        {saving ? 'Cuvanje...' : 'Sacuvaj'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default CMSCategoriesPage;
