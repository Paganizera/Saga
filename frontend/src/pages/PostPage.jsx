import {useCallback, useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {api, formatDate} from '../api.js';
import {useAuth} from '../auth.jsx';
import Markdown from '../Markdown.jsx';

export default function PostPage() {
    const {id} = useParams();
    const {isAdmin} = useAuth();
    const navigate = useNavigate();
    const [post, setPost] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        api.getPost(id).then(setPost).catch((e) => setError(e.status === 404 ? 'Post não encontrado.' : e.message));
    }, [id]);

    async function removePost() {
        if (!window.confirm('Excluir este post e todos os comentários?')) return;
        try {
            await api.deletePost(id);
            navigate('/');
        } catch (e) {
            setError(e.message);
        }
    }

    if (error) return <p className="state error">{error} <Link to="/">Voltar ao início</Link></p>;
    if (!post) return <p className="state">Carregando post…</p>;

    return (
        <article>
            <Link to="/" className="back">Todos os posts</Link>
            <h1>{post.title}</h1>
            <p className="meta">{post.author}, {formatDate(post.publishedAt)}</p>
            {isAdmin && (
                <div className="row">
                    <Link to={`/admin/posts/${id}/editar`}>Editar</Link>
                    <button className="link danger" onClick={removePost}>Excluir post</button>
                </div>
            )}
            <div className="prose"><Markdown>{post.content}</Markdown></div>
            <Comments postId={id}/>
        </article>
    );
}

function Comments({postId}) {
    const {isAdmin} = useAuth();
    const [data, setData] = useState(null);
    const [page, setPage] = useState(0);
    const [author, setAuthor] = useState('');
    const [content, setContent] = useState('');
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    const load = useCallback(
        (p) => api.listComments(postId, p).then(setData).catch((e) => setError(e.message)),
        [postId]
    );
    useEffect(() => {
        load(page);
    }, [load, page]);

    async function submit(e) {
        e.preventDefault();
        setBusy(true);
        setError('');
        try {
            await api.createComment(postId, {author, content});
            setContent('');
            if (page === 0) await load(0); else setPage(0);
        } catch (err) {
            setError(err.message);
        } finally {
            setBusy(false);
        }
    }

    async function remove(cid) {
        if (!window.confirm('Excluir este comentário?')) return;
        try {
            await api.deleteComment(cid);
            await load(page);
        } catch (e) {
            setError(e.message);
        }
    }

    const items = data?.content ?? [];
    return (
        <section className="comments">
            <h2>Comentários{data ? ` (${data.totalElements})` : ''}</h2>
            {!data && !error && <p className="state">Carregando comentários…</p>}
            {data && items.length === 0 &&
                <p className="state">Nenhum comentário ainda. Seja a primeira pessoa a comentar.</p>}
            <ul>
                {items.map((c) => (
                    <li key={c.id}>
                        <p className="meta">{c.author}, {formatDate(c.publishedAt)}</p>
                        <p className="body">{c.content}</p>
                        {isAdmin && <button className="link danger" onClick={() => remove(c.id)}>Excluir</button>}
                    </li>
                ))}
            </ul>
            {data && data.totalPages > 1 && (
                <div className="row pager">
                    <button className="link" disabled={data.first} onClick={() => setPage(page - 1)}>Anteriores</button>
                    <span>Página {data.number + 1} de {data.totalPages}</span>
                    <button className="link" disabled={data.last} onClick={() => setPage(page + 1)}>Próximos</button>
                </div>
            )}
            <form className="form" onSubmit={submit}>
                <h3>Deixe um comentário</h3>
                <label>Seu nome<input value={author} onChange={(e) => setAuthor(e.target.value)} required
                                      maxLength={80}/></label>
                <label>Comentário<textarea rows={4} maxLength={2000} value={content}
                                           onChange={(e) => setContent(e.target.value)} required/></label>
                {error && <p className="error" role="alert">{error}</p>}
                <button className="primary" disabled={busy}>{busy ? 'Enviando…' : 'Publicar comentário'}</button>
            </form>
        </section>
    );
}
