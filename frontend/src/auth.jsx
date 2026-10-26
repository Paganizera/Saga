import {createContext, useContext, useState, useCallback} from 'react';
import {api} from './api.js';

const AuthContext = createContext(null);

function decode(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        if (payload.exp * 1000 < Date.now()) return null;
        return {username: payload.sub, role: payload.role};
    } catch {
        return null;
    }
}

function initialUser() {
    const token = localStorage.getItem('token');
    if (!token) return null;
    const user = decode(token);
    if (!user) localStorage.removeItem('token');
    return user;
}

export function AuthProvider({children}) {
    const [user, setUser] = useState(initialUser);

    const login = useCallback(async (username, password) => {
        const {token} = await api.login(username, password);
        localStorage.setItem('token', token);
        setUser(decode(token));
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem('token');
        setUser(null);
    }, []);

    const isAdmin = user?.role === 'ROLE_ADMIN';
    return <AuthContext.Provider value={{user, isAdmin, login, logout}}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
