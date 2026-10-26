import {Link, NavLink, Navigate, Route, Routes} from 'react-router-dom';
import {useAuth} from './auth.jsx';
import Home from './pages/Home.jsx';
import PostPage from './pages/PostPage.jsx';
import Login from './pages/Login.jsx';
import PostEditor from './pages/PostEditor.jsx';

function AdminOnly({children}) {
    const {isAdmin} = useAuth();
    return isAdmin ? children : <Navigate to="/login" replace/>;
}

export default function App() {
    const {user, isAdmin, logout} = useAuth();
    return (
        <div className="paper">
            <header className="topbar">
                <Link to="/" className="brand">Saga</Link>
                <nav>
                    {isAdmin && <NavLink to="/admin/novo">Novo post</NavLink>}
                    {user ? (
                        <button className="link" onClick={logout}>Sair ({user.username})</button>
                    ) : (
                        <NavLink to="/login">Entrar</NavLink>
                    )}
                </nav>
            </header>
            <main>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/posts/:id" element={<PostPage/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/admin/novo" element={<AdminOnly><PostEditor/></AdminOnly>}/>
                    <Route path="/admin/posts/:id/editar" element={<AdminOnly><PostEditor/></AdminOnly>}/>
                    <Route path="*"
                           element={<p className="state">Página não encontrada. <Link to="/">Voltar ao início</Link>
                           </p>}/>
                </Routes>
            </main>
        </div>
    );
}
