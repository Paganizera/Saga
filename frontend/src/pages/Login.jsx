import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '../auth.jsx';

export default function Login() {
    const {login} = useAuth();
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    async function submit(e) {
        e.preventDefault();
        setBusy(true);
        setError('');
        try {
            await login(username, password);
            navigate('/');
        } catch (err) {
            setError(err.status === 401 ? 'Usuário ou senha incorretos.' : err.message);
        } finally {
            setBusy(false);
        }
    }

    return (
        <form className="form narrow" onSubmit={submit}>
            <h1>Entrar</h1>
            <label>Usuário<input value={username} onChange={(e) => setUsername(e.target.value)} required
                                 autoFocus/></label>
            <label>Senha<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required/></label>
            {error && <p className="error" role="alert">{error}</p>}
            <button className="primary" disabled={busy}>{busy ? 'Entrando…' : 'Entrar'}</button>
        </form>
    );
}
