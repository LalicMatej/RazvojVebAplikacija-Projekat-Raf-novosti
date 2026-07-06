import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense, type ReactElement } from 'react';
import { Spinner, Container } from 'react-bootstrap';
import NavigationBar from './components/NavigationBar';
import { isAuthenticated, isAdmin } from './auth';

const LoginPage      = lazy(() => import('./pages/LoginPage'));
const HomePage       = lazy(() => import('./pages/HomePage'));
const MostReadPage   = lazy(() => import('./pages/MostReadPage'));
const NewsListPage   = lazy(() => import('./pages/NewsListPage'));
const NewsDetailPage = lazy(() => import('./pages/NewsDetailPage'));
const CMSCategoriesPage = lazy(() => import('./pages/cms/CMSCategoriesPage'));
const CMSPage        = lazy(() => import('./pages/cms/CMSPage'));
const CMSUsersPage   = lazy(() => import('./pages/cms/CMSUsersPage'));

const PrivateRoute = ({ element }: { element: ReactElement }) =>
    isAuthenticated() ? element : <Navigate to="/login" />;

const AdminRoute = ({ element }: { element: ReactElement }) =>
    isAdmin() ? element : <Navigate to="/" />;

const Loading = () => (
    <Container className="d-flex justify-content-center mt-5">
        <Spinner animation="border" role="status">
            <span className="visually-hidden">Ucitavanje...</span>
        </Spinner>
    </Container>
);

function App() {
    return (
        <Router>
            <NavigationBar />
            <main style={{ paddingTop: '1rem' }}>
                <Suspense fallback={<Loading />}>
                    <Routes>
                        <Route path="/"          element={<HomePage />} />
                        <Route path="/login"     element={<LoginPage />} />
                        <Route path="/most-read" element={<MostReadPage />} />
                        <Route path="/news"      element={<NewsListPage />} />
                        <Route path="/news/category/:categoryId" element={<NewsListPage />} />
                        <Route path="/news/tag/:tagId" element={<NewsListPage />} />
                        <Route path="/news/:id"  element={<NewsDetailPage />} />
                        <Route path="/cms"       element={<PrivateRoute element={<CMSCategoriesPage />} />} />
                        <Route path="/cms/news"  element={<PrivateRoute element={<CMSPage />} />} />
                        <Route path="/cms/users" element={<AdminRoute element={<CMSUsersPage />} />} />
                        <Route path="*"          element={<Navigate to="/" />} />
                    </Routes>
                </Suspense>
            </main>
        </Router>
    );
}

export default App;
