import { useState, useEffect } from 'react';
import { Modal, Form, Button, Alert } from 'react-bootstrap';
import axios from 'axios';
import type { News, Category, Tag } from '../../types';
import { getAllCategories } from '../../api/categoryApi';
import { createNews, updateNews, type NewsRequest } from '../../api/newsApi';

interface Props {
    show: boolean;
    onHide: () => void;
    onSaved: () => void;
    editNews?: News | null;
}

interface FormState {
    title: string;
    content: string;
    categoryId: number | '';
    tagsInput: string;
    error: string;
}

const createFormState = (editNews?: News | null): FormState => ({
    title: editNews?.title ?? '',
    content: editNews?.content ?? '',
    categoryId: editNews?.category.id ?? '',
    tagsInput: editNews
        ? editNews.tags.map((t: Tag) => t.name).join(', ')
        : '',
    error: '',
});

const NewsFormModal = ({ show, onHide, onSaved, editNews }: Props) => {
    const [formState, setFormState]   = useState<FormState>(() => createFormState(editNews));
    const [categories, setCategories] = useState<Category[]>([]);
    const [saving, setSaving]         = useState(false);

    useEffect(() => {
        getAllCategories()
            .then(setCategories)
            .catch(() => {});
    }, []);

    const handleEnter = () => {
        setFormState(createFormState(editNews));
    };

    const handleClose = () => {
        setFormState(createFormState(editNews));
        onHide();
    };

    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault();
        const { title, content, categoryId, tagsInput } = formState;

        if (!title.trim() || !content.trim() || categoryId === '') {
            setFormState((current) => ({
                ...current,
                error: 'Naslov, sadrzaj i kategorija su obavezni.',
            }));
            return;
        }

        const selectedCategory = categories.find((c) => c.id === Number(categoryId));
        if (!selectedCategory) {
            setFormState((current) => ({
                ...current,
                error: 'Izaberite kategoriju.',
            }));
            return;
        }

        const tags = tagsInput
            .split(',')
            .map((t) => t.trim())
            .filter((t) => t.length > 0)
            .map((name) => ({ name }));

        const data: NewsRequest = {
            title,
            content,
            categoryId: selectedCategory.id,
            tags,
        };

        setSaving(true);
        try {
            if (editNews) {
                await updateNews(editNews.id, data);
            } else {
                await createNews(data);
            }
            onSaved();
            handleClose();
        } catch (caughtError) {
            const message = axios.isAxiosError(caughtError)
                ? caughtError.response?.data?.message
                : null;
            setFormState((current) => ({
                ...current,
                error: typeof message === 'string' && message.trim()
                    ? message
                    : 'Greska pri cuvanju vesti.',
            }));
        } finally {
            setSaving(false);
        }
    };

    return (
        <Modal
            show={show}
            onHide={handleClose}
            onEnter={handleEnter}
            size="lg"
            backdrop="static"
        >
            <Modal.Header closeButton>
                <Modal.Title>{editNews ? 'Izmeni vest' : 'Nova vest'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {formState.error && <Alert variant="danger">{formState.error}</Alert>}
                <Form onSubmit={handleSubmit} id="news-form">
                    <Form.Group className="mb-3">
                        <Form.Label>Naslov</Form.Label>
                        <Form.Control
                            type="text"
                            value={formState.title}
                            onChange={(e) => setFormState((current) => ({
                                ...current,
                                title: e.target.value,
                            }))}
                            required
                            maxLength={200}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Kategorija</Form.Label>
                        <Form.Select
                            value={formState.categoryId}
                            onChange={(e) => setFormState((current) => ({
                                ...current,
                                categoryId: e.target.value === '' ? '' : Number(e.target.value),
                            }))}
                            required
                        >
                            <option value="">-- Izaberite kategoriju --</option>
                            {categories.map((cat) => (
                                <option key={cat.id} value={cat.id}>
                                    {cat.name}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Sadrzaj</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={8}
                            value={formState.content}
                            onChange={(e) => setFormState((current) => ({
                                ...current,
                                content: e.target.value,
                            }))}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Tagovi (odvojeni zarezom)</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="politika, sport, tehnologija"
                            value={formState.tagsInput}
                            onChange={(e) => setFormState((current) => ({
                                ...current,
                                tagsInput: e.target.value,
                            }))}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Otkazi
                </Button>
                <Button
                    variant="primary"
                    type="submit"
                    form="news-form"
                    disabled={saving}
                >
                    {saving ? 'Cuvanje...' : 'Sacuvaj'}
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default NewsFormModal;
