export interface JwtPayload {
    sub: string;       // email
    firstName?: string;
    lastName?: string;
    role: string;
    userId: number;
    exp: number;
}

export const getToken = (): string | null => {
    return localStorage.getItem('jwt');
};

export const setToken = (token: string): void => {
    localStorage.setItem('jwt', token);
};

export const removeToken = (): void => {
    localStorage.removeItem('jwt');
};

const decodeBase64UrlUtf8 = (value: string): string => {
    const base64 = value
        .replace(/-/g, '+')
        .replace(/_/g, '/')
        .padEnd(Math.ceil(value.length / 4) * 4, '=');

    const binary = atob(base64);
    const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0));
    return new TextDecoder('utf-8').decode(bytes);
};

export const getPayload = (): JwtPayload | null => {
    const token = getToken();
    if (!token) return null;
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        return JSON.parse(decodeBase64UrlUtf8(parts[1])) as JwtPayload;
    } catch {
        return null;
    }
};

export const isAuthenticated = (): boolean => {
    const payload = getPayload();
    if (!payload || !payload.exp) return false;
    return Date.now() < payload.exp * 1000;
};

export const isAdmin = (): boolean => {
    const payload = getPayload();
    return payload?.role === 'ADMIN';
};

export const getDisplayName = (): string | null => {
    const payload = getPayload();
    if (!payload) return null;

    const fullName = [payload.firstName, payload.lastName]
        .filter(Boolean)
        .join(' ')
        .trim();

    return fullName || payload.sub || null;
};

export const logout = (navigate: (path: string) => void): void => {
    removeToken();
    navigate('/login');
};
