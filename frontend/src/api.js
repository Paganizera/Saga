const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function request(path, {method = 'GET', body} = {}) {
    const headers = {};
    if (body) headers['Content-Type'] = 'application/json';
    const token = localStorage.getItem('token');
    if (token) headers.Authorization = `Bearer ${token}`;

    const res = await fetch(BASE + path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });
    if (!res.ok) {
        let message = 'Algo deu errado. Tente novamente.';
        if (res.status === 429) message = 'Muitas tentativas seguidas. Aguarde um minuto e tente de novo.';
        else if (res.status === 401) message = 'Sessão expirada ou credenciais inválidas.';
        else if (res.status === 403) message = 'Você não tem permissão para fazer isso.';
        else {
            try {
                message = (await res.json()).message || message;
            } catch { /* sem corpo */
            }
        }
        const err = new Error(message);
        err.status = res.status;
        throw err;
    }
    return res.status === 204 ? null : res.json();
}

export const api = {
    login: (username, password) => request('/auth/login', {method: 'POST', body: {username, password}}),
    listPosts: (page = 0, size = 10) => request(`/posts?page=${page}&size=${size}`),
    getPost: (id) => request(`/posts/${id}`),
    createPost: (data) => request('/posts', {method: 'POST', body: data}),
    updatePost: (id, data) => request(`/posts/${id}`, {method: 'PUT', body: data}),
    deletePost: (id) => request(`/posts/${id}`, {method: 'DELETE'}),
    listComments: (postId, page = 0, size = 10) => request(`/posts/${postId}/commentaries?page=${page}&size=${size}`),
    createComment: (postId, data) => request(`/posts/${postId}/commentaries`, {method: 'POST', body: data}),
    deleteComment: (id) => request(`/commentaries/${id}`, {method: 'DELETE'}),
};

export const formatDate = (iso) =>
    new Date(iso).toLocaleDateString('pt-BR', {day: 'numeric', month: 'long', year: 'numeric'});
