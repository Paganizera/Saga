import {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import {api, formatDate} from '../api.js';

const countLabel = (n) => `${n} ${n === 1 ? 'comentário' : 'comentários'}`;

export default function Home() {
    const [data, setData] = useState(null);
    const [page, setPage] = useState(0);
    const [error, setError] = useState('');

    useEffect(() => {
        setError('');
        api.listPosts(page)
            .then(setData)
            .catch(() => setError('Não foi possível carregar os posts. Verifique se o servidor está no ar.'));
    }, [page]);

    if (error) return <p className="state error">{error}</p>;
    if (!data) return <p className="state">Carregando posts…</p>;
    if (data.content.length === 0) return <p className="state">Ainda não há posts publicados.</p>;

    // O post mais recente ganha destaque apenas na primeira página.
    const featured = page === 0 ? data.content[0] : null;
    const rows = page === 0 ? data.content.slice(1) : data.content;

    return (
        <>
            {featured && (
                <Link to={`/posts/${featured.id}`} className="feature">
                    <span className="date">{formatDate(featured.publishedAt)}</span>
                    <h2>{featured.title}</h2>
                    {featured.excerpt && <p className="excerpt">{featured.excerpt}</p>}
                    <span className="more">Ler o texto completo</span>
                </Link>
            )}
            {rows.map((p) => (
                <Link key={p.id} to={`/posts/${p.id}`} className="item">
                    <div className="main">
                        <h2>{p.title}</h2>
                        {p.excerpt && <p>{p.excerpt}</p>}
                        <span className="date">{countLabel(p.commentaryCount)}</span>
                    </div>
                    <span className="date">{formatDate(p.publishedAt)}</span>
                </Link>
            ))}
            {data.totalPages > 1 && (
                <div className="row pager">
                    <button className="link" disabled={data.first} onClick={() => setPage(page - 1)}>Mais recentes
                    </button>
                    <span>Página {data.number + 1} de {data.totalPages}</span>
                    <button className="link" disabled={data.last} onClick={() => setPage(page + 1)}>Mais antigos
                    </button>
                </div>
            )}
        </>
    );
}
