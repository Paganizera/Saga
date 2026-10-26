import {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {api} from '../api.js';
import Markdown from '../Markdown.jsx';

export default function PostEditor() {
    const {id} = useParams();
    const editing = Boolean(id);
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tab, setTab] = useState('write');
    const [loading, setLoading] = useState(editing);
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        if (!editing) return;
        api.getPost(id)
            .then((p) => {
                setTitle(p.title);
                setContent(p.content);
            })
            .catch((e) => setError(e.message))
            .finally(() => setLoading(false));
    }, [id, editing]);

    async function submit(e) {
        e.preventDefault();
        setBusy(true);
        setError('');
        try {
            // O autor é definido pelo backend a partir do token.
            const saved = editing
                ? await api.updatePost(id, {title, content})
                : await api.createPost({title, content});
            navigate(`/posts/${saved.id}`);
        } catch (err) {
            setError(err.message);
            setBusy(false);
        }
    }

    if (loading) return <p className="state">Carregando post…</p>;

    return (
        <form className="form" onSubmit={submit}>
            <h1>{editing ? 'Editar post' : 'Novo post'}</h1>
            <label>Título<input value={title} onChange={(e) => setTitle(e.target.value)} required
                                maxLength={200}/></label>
            <div>
                <div className="tabs" role="tablist">
                    <button type="button" role="tab" aria-selected={tab === 'write'}
                            onClick={() => setTab('write')}>Escrever
                    </button>
                    <button type="button" role="tab" aria-selected={tab === 'preview'}
                            onClick={() => setTab('preview')}>Visualizar
                    </button>
                </div>
                {tab === 'write' ? (
                    <label style={{marginTop: '.75rem'}}>
                        <textarea rows={18} value={content} onChange={(e) => setContent(e.target.value)} required
                                  maxLength={50000} aria-label="Conteúdo em Markdown"/>
                        <span className="hint">Markdown com tabelas, listas de tarefas e tachado (GFM). HTML não é interpretado.</span>
                    </label>
                ) : (
                    <div className="prose preview">
                        {content.trim() ? <Markdown>{content}</Markdown> :
                            <p className="state">Nada para visualizar ainda.</p>}
                    </div>
                )}
            </div>
            {error && <p className="error" role="alert">{error}</p>}
            <div className="row">
                <button className="primary"
                        disabled={busy}>{busy ? 'Salvando…' : editing ? 'Salvar alterações' : 'Publicar'}</button>
                <button type="button" className="link" onClick={() => navigate(-1)}>Cancelar</button>
            </div>
        </form>
    );
}
