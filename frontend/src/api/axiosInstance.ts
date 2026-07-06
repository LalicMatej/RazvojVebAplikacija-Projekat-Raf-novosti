import axios from 'axios';
import { getToken } from '../auth';

const _axios = axios.create({
    baseURL: 'http://localhost:8080/raf-news',
    timeout: 60000,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

_axios.interceptors.request.use(
    (request) => {
        const token = getToken();
        if (token) {
            request.headers.Authorization = `Bearer ${token}`;
        }
        return request;
    },
    (error) => Promise.reject(error)
);

_axios.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401 && window.location.pathname !== '/login') {
            localStorage.removeItem('jwt');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default _axios;
