import { useState } from 'react';
import { useEffect } from 'react';
import { Navbar, Nav, Container, Button, Form, InputGroup, NavDropdown } from 'react-bootstrap';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { getDisplayName, isAuthenticated, isAdmin, logout } from '../auth';
import { getAllCategories } from '../api/categoryApi';
import type { Category } from '../types';

const NavigationBar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const auth = isAuthenticated();
    const admin = isAdmin();
    const displayName = getDisplayName();
    const [cmsSearch, setCmsSearch] = useState('');
    const [categories, setCategories] = useState<Category[]>([]);

    const isActive = (path: string) => location.pathname === path ? 'active' : '';

    useEffect(() => {
        getAllCategories()
            .then(setCategories)
            .catch(() => {});
    }, []);

    const handleCmsSearch = (e: React.FormEvent) => {
        e.preventDefault();
        const query = cmsSearch.trim();
        if (location.pathname.startsWith('/cms')) {
            navigate(query ? `/cms/news?query=${encodeURIComponent(query)}` : '/cms/news');
            return;
        }

        navigate(query ? `/news?query=${encodeURIComponent(query)}` : '/news');
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" sticky="top" className="app-navbar">
            <Container>
                <Navbar.Brand as={Link} to="/">RAF News</Navbar.Brand>
                <Navbar.Toggle aria-controls="main-nav" />
                <Navbar.Collapse id="main-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/" className={isActive('/')}>
                            Pocetna
                        </Nav.Link>
                        <Nav.Link as={Link} to="/news" className={isActive('/news')}>
                            Vesti
                        </Nav.Link>
                        <Nav.Link as={Link} to="/most-read" className={isActive('/most-read')}>
                            Najcitanije
                        </Nav.Link>
                        <NavDropdown title="Kategorije" id="public-categories-nav">
                            {categories.map((category) => (
                                <NavDropdown.Item
                                    key={category.id}
                                    as={Link}
                                    to={`/news/category/${category.id}`}
                                >
                                    {category.name}
                                </NavDropdown.Item>
                            ))}
                        </NavDropdown>
                        {auth && (
                            <>
                                <Nav.Link as={Link} to="/cms" className={isActive('/cms')}>
                                    Kategorije
                                </Nav.Link>
                                <Nav.Link as={Link} to="/cms/news" className={isActive('/cms/news')}>
                                    CMS Vesti
                                </Nav.Link>
                            </>
                        )}
                        {admin && (
                            <Nav.Link as={Link} to="/cms/users" className={isActive('/cms/users')}>
                                Korisnici
                            </Nav.Link>
                        )}
                    </Nav>
                    <Form onSubmit={handleCmsSearch} className="me-3">
                        <InputGroup size="sm">
                            <Form.Control
                                type="search"
                                placeholder={
                                    location.pathname.startsWith('/cms')
                                        ? 'Pretraga CMS vesti'
                                        : 'Pretraga vesti'
                                }
                                value={cmsSearch}
                                onChange={(e) => setCmsSearch(e.target.value)}
                            />
                            <Button type="submit" variant="outline-light">
                                Trazi
                            </Button>
                        </InputGroup>
                    </Form>
                    <Nav>
                        {auth ? (
                            <div className="d-flex align-items-center gap-2">
                                <span className="text-light small">{displayName}</span>
                                <Button
                                    variant="outline-danger"
                                    onClick={() => logout(navigate)}
                                >
                                    Odjavi se
                                </Button>
                            </div>
                        ) : (
                            <Button
                                variant="outline-success"
                                onClick={() => navigate('/login')}
                            >
                                Prijavi se
                            </Button>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default NavigationBar;
