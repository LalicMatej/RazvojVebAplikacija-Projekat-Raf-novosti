import { useState, useEffect, useCallback } from 'react';
import {
    Container, Table, Button, Badge,
    Spinner, Alert, Pagination, Modal, Form
} from 'react-bootstrap';
import {
    getUsers,
    createUser,
    updateUser,
    toggleUserStatus,
    type UserRequest,
    type UpdateUserRequest,
} from '../../api/userApi';
import type { User } from '../../types';

interface UserPageResponse {
    data: User[];
    totalPages: number;
    totalCount: number;
}

interface UserFormState {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    role: 'ADMIN' | 'CONTENT_CREATOR';
}

const EMPTY_FORM: UserFormState = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'CONTENT_CREATOR',
};

const CMSUsersPage = () => {
    const [usersPage, setUsersPage]     = useState<UserPageResponse | null>(null);
    const [users, setUsers]             = useState<User[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading]         = useState(true);
    const [error, setError]             = useState('');
    const [showModal, setShowModal]     = useState(false);
    const [editUser, setEditUser]       = useState<User | null>(null);
    const [form, setForm]               = useState<UserFormState>(EMPTY_FORM);
    const [saving, setSaving]           = useState(false);
    const [formError, setFormError]     = useState('');

    const fetchUsers = useCallback(async (page: number) => {
        setLoading(true);
        try {
            const data = await getUsers(page);
            setUsersPage(data);
            setUsers(data.data);
        } catch {
            setError('Greska pri ucitavanju korisnika.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            fetchUsers(currentPage).catch(() => {});
        }, 0);

        return () => window.clearTimeout(timeoutId);
    }, [fetchUsers, currentPage]);

    const handleNew = () => {
        setEditUser(null);
        setForm(EMPTY_FORM);
        setFormError('');
        setShowModal(true);
    };

    const handleEdit = (user: User) => {
        setEditUser(user);
        setForm({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            password: '',
            confirmPassword: '',
            role: user.role,
        });
        setFormError('');
        setShowModal(true);
    };

    const handleToggleStatus = async (id: number) => {
        try {
            await toggleUserStatus(id);
            await fetchUsers(currentPage);
        } catch {
            setError('Greska pri promeni statusa.');
        }
    };

    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault();
        setSaving(true);
        setFormError('');

        if (!editUser && form.password !== form.confirmPassword) {
            setFormError('Lozinka i potvrda lozinke moraju da se poklapaju.');
            setSaving(false);
            return;
        }

        try {
            if (editUser) {
                const payload: UpdateUserRequest = {
                    firstName: form.firstName,
                    lastName: form.lastName,
                    email: form.email,
                    role: form.role,
                };
                await updateUser(editUser.id, payload);
            } else {
                const payload: UserRequest = {
                    firstName: form.firstName,
                    lastName: form.lastName,
                    email: form.email,
                    password: form.password,
                    role: form.role,
                };
                await createUser(payload);
            }
            setShowModal(false);
            setForm(EMPTY_FORM);
            await fetchUsers(currentPage);
        } catch {
            setFormError('Greska pri cuvanju korisnika.');
        } finally {
            setSaving(false);
        }
    };

    const renderPagination = () => {
        if (!usersPage || usersPage.totalPages <= 1) return null;
        const items = [];
        for (let i = 1; i <= usersPage.totalPages; i++) {
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
        return (
            <Pagination className="justify-content-center mt-3">{items}</Pagination>
        );
    };

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Upravljanje korisnicima</h2>
                <Button variant="primary" onClick={handleNew}>
                    + Novi korisnik
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
            ) : users.length === 0 ? (
                <Alert variant="info">Nema korisnika.</Alert>
            ) : (
                <>
                    <Table striped bordered hover responsive>
                        <thead className="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Ime</th>
                                <th>Prezime</th>
                                <th>Email</th>
                                <th>Uloga</th>
                                <th>Status</th>
                                <th>Akcije</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.firstName}</td>
                                    <td>{user.lastName}</td>
                                    <td>{user.email}</td>
                                    <td>
                                        <Badge bg={user.role === 'ADMIN' ? 'danger' : 'secondary'}>
                                            {user.role}
                                        </Badge>
                                    </td>
                                    <td>
                                        <Badge bg={user.status === 'ACTIVE' ? 'success' : 'warning'}>
                                            {user.status}
                                        </Badge>
                                    </td>
                                    <td>
                                        <div className="d-flex gap-2">
                                            <Button
                                                size="sm"
                                                variant="outline-primary"
                                                onClick={() => handleEdit(user)}
                                            >
                                                Izmeni
                                            </Button>
                                            {user.role === 'CONTENT_CREATOR' && (
                                                <Button
                                                    size="sm"
                                                    variant={
                                                        user.status === 'ACTIVE'
                                                            ? 'outline-warning'
                                                            : 'outline-success'
                                                    }
                                                    onClick={() => handleToggleStatus(user.id)}
                                                >
                                                    {user.status === 'ACTIVE' ? 'Deaktiviraj' : 'Aktiviraj'}
                                                </Button>
                                            )}
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
                        {editUser ? 'Izmeni korisnika' : 'Novi korisnik'}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {formError && <Alert variant="danger">{formError}</Alert>}
                    <Form onSubmit={handleSubmit} id="user-form">
                        <Form.Group className="mb-3">
                            <Form.Label>Ime</Form.Label>
                            <Form.Control
                                type="text"
                                value={form.firstName}
                                onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Prezime</Form.Label>
                            <Form.Control
                                type="text"
                                value={form.lastName}
                                onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                value={form.email}
                                onChange={(e) => setForm({ ...form, email: e.target.value })}
                                required
                            />
                        </Form.Group>
                        {!editUser && (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>Lozinka</Form.Label>
                                    <Form.Control
                                        type="password"
                                        value={form.password}
                                        onChange={(e) => setForm({ ...form, password: e.target.value })}
                                        required
                                        minLength={8}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Potvrda lozinke</Form.Label>
                                    <Form.Control
                                        type="password"
                                        value={form.confirmPassword}
                                        onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
                                        required
                                        minLength={8}
                                    />
                                </Form.Group>
                            </>
                        )}
                        <Form.Group className="mb-3">
                            <Form.Label>Uloga</Form.Label>
                            <Form.Select
                                value={form.role}
                                onChange={(e) =>
                                    setForm({
                                        ...form,
                                        role: e.target.value as 'ADMIN' | 'CONTENT_CREATOR',
                                    })
                                }
                            >
                                <option value="CONTENT_CREATOR">CONTENT_CREATOR</option>
                                <option value="ADMIN">ADMIN</option>
                            </Form.Select>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Otkazi
                    </Button>
                    <Button
                        variant="primary"
                        type="submit"
                        form="user-form"
                        disabled={saving}
                    >
                        {saving ? 'Cuvanje...' : 'Sacuvaj'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default CMSUsersPage;
